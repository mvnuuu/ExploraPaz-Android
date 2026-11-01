package cl.duoc.mpazpro.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazpro.model.*
import cl.duoc.mpazpro.repository.MPAZRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Toda validación y toda operación con datos se gestiona fuera de los composables. */
class MPAZViewModel(private val repository: MPAZRepository) : ViewModel() {
    // Guía 11: MutableStateFlow compartido entre pantallas y collectAsState en Compose.
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    init { viewModelScope.launch { repository.state.collect { _state.value = it } } }

    private var attemptedSave by mutableStateOf(false)
    private var attemptedNote by mutableStateOf(false)

    var noteDraft by mutableStateOf("")
        private set
    var noteError by mutableStateOf("")
        private set
    var lastSavedTitle by mutableStateOf("")
        private set

    var form by mutableStateOf(UnitForm())
        private set
    var errors by mutableStateOf(UnitErrors())
        private set
    var assignmentLimit by mutableStateOf("2")
        private set
    var startDate by mutableStateOf("")
        private set
    var endDate by mutableStateOf("")
        private set
    var dateError by mutableStateOf("")
        private set
    var assignmentError by mutableStateOf("")
        private set
    var mediaError by mutableStateOf("")
        private set
    var quizError by mutableStateOf("")
        private set

    fun beginNew() { form = UnitForm(); errors = UnitErrors(); mediaError = ""; attemptedSave = false }
    fun beginEdit(unit: DidacticUnit) {
        form = UnitForm.from(unit); errors = UnitErrors(); mediaError = ""; attemptedSave = false
    }
    fun updateForm(newValue: UnitForm) {
        form = newValue
        // Tras el primer intento de guardado, cada cambio revalida todos los campos.
        // No se borran errores que todavía siguen presentes.
        if (attemptedSave) validateUnit()
    }
    fun updateLimit(text: String) { assignmentLimit = text; assignmentError = "" }
    fun updateDates(from: String, to: String) { startDate = from; endDate = to; dateError = "" }
    fun updateStartDate(text: String) { startDate = text; dateError = "" }
    fun updateEndDate(text: String) { endDate = text; dateError = "" }

    private fun dateIsValid(text: String): Boolean {
        if (!text.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return false
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            formatter.isLenient = false
            formatter.parse(text) != null
        } catch (_: Exception) { false }
    }

    fun validateUnit(): Boolean {
        attemptedSave = true
        val f = form
        val alternatives = listOf(f.choice1, f.choice2, f.choice3, f.choice4).map { it.trim() }
        errors = UnitErrors(
            title = if (f.title.trim().length < 4) "Escribe un título de 4 caracteres o más" else "",
            code = if (f.objectiveCode.isBlank()) "Ingresa el código del OA" else "",
            objective = if (f.objective.isBlank()) "Describe el objetivo de aprendizaje" else "",
            explanation = if (f.explanation.isBlank()) "Agrega una explicación" else "",
            example = if (f.example.isBlank()) "Agrega un ejemplo guiado" else "",
            question = if (f.question.isBlank()) "Ingresa una pregunta" else "",
            choices = when {
                alternatives.any { it.isBlank() } -> "Completa las cuatro alternativas"
                alternatives.distinct().size != 4 -> "Las alternativas deben ser diferentes"
                else -> ""
            }
        )
        return errors.isValid
    }

    fun saveUnit(onSuccess: () -> Unit) {
        if (!validateUnit()) return
        val newId = if (form.id != 0) form.id else (state.value.units.maxOfOrNull { it.id } ?: 0) + 1
        // Modificar una unidad publicada vuelve a borrador, obligando nueva autorización.
        val unit = form.copy(id = newId).toUnit(UnitStatus.BORRADOR)
        viewModelScope.launch {
            repository.saveUnit(unit)
            lastSavedTitle = unit.title
            onSuccess()
        }
    }

    fun clearSavedFeedback() { lastSavedTitle = "" }

    fun beginNote(existing: String) {
        noteDraft = existing
        noteError = ""
        attemptedNote = false
    }
    fun updateNote(text: String) {
        noteDraft = text
        if (attemptedNote) noteError = validateNote(text)
    }
    private fun validateNote(text: String): String =
        if (text.trim().length < 10) "La observación debe tener 10 caracteres o más" else ""

    fun saveNote(id: Int, onSuccess: () -> Unit) {
        attemptedNote = true
        noteError = validateNote(noteDraft)
        if (noteError.isNotBlank()) return
        viewModelScope.launch { repository.saveTeacherNote(id, noteDraft.trim()); onSuccess() }
    }

    fun authorize(unit: DidacticUnit) {
        if (unit.status != UnitStatus.BORRADOR) return
        viewModelScope.launch { repository.changeStatus(unit.id, UnitStatus.AUTORIZADO) }
    }
    fun publish(unit: DidacticUnit) {
        if (unit.status != UnitStatus.AUTORIZADO) return
        viewModelScope.launch { repository.changeStatus(unit.id, UnitStatus.PUBLICADO) }
    }
    fun authorizeDisabled(unit: DidacticUnit) {
        if (unit.status != UnitStatus.DESHABILITADO) return
        viewModelScope.launch { repository.changeStatus(unit.id, UnitStatus.BORRADOR) }
    }
    fun disable(unit: DidacticUnit) {
        viewModelScope.launch { repository.changeStatus(unit.id, UnitStatus.DESHABILITADO) }
    }

    fun assign(unit: DidacticUnit, onSuccess: () -> Unit) {
        val count = assignmentLimit.toIntOrNull()
        assignmentError = when {
            unit.status != UnitStatus.PUBLICADO -> "La unidad debe estar publicada"
            count == null || count !in 1..5 -> "Los intentos deben estar entre 1 y 5"
            else -> ""
        }
        dateError = when {
            startDate.isBlank() && endDate.isBlank() -> ""
            !dateIsValid(startDate) || !dateIsValid(endDate) -> "Completa ambas fechas válidas AAAA-MM-DD"
            startDate > endDate -> "La fecha inicial debe ser anterior a la final"
            else -> ""
        }
        if (assignmentError.isNotEmpty() || dateError.isNotEmpty() || count == null) return
        viewModelScope.launch { repository.assign(unit.id, count, startDate, endDate); onSuccess() }
    }

    fun canStudentOpen(id: Int): Boolean {
        val unit = state.value.units.firstOrNull { it.id == id } ?: return false
        val assignment = state.value.assignment(id) ?: return false
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
        return unit.status == UnitStatus.PUBLICADO &&
            (assignment.startDate.isBlank() || today >= assignment.startDate) &&
            (assignment.endDate.isBlank() || today <= assignment.endDate)
    }

    fun markRead(id: Int) {
        if (!canStudentOpen(id)) return
        viewModelScope.launch { repository.markRead(id) }
    }

    fun canAttempt(id: Int): Boolean {
        val current = state.value
        val assignment = current.assignment(id) ?: return false
        return canStudentOpen(id) && current.progress(id).read && current.progress(id).attempts < assignment.maxAttempts
    }

    fun clearQuizError() { quizError = "" }

    fun submit(id: Int, choice: Int, onSaved: () -> Unit) {
        quizError = when {
            choice !in 0..3 -> "Debes seleccionar una alternativa"
            !canAttempt(id) -> "Prueba no disponible o intentos agotados"
            else -> ""
        }
        if (quizError.isNotEmpty()) return
        val unit = state.value.units.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            if (repository.recordAttempt(id, choice, choice == unit.correctChoice)) {
                onSaved()
            } else {
                quizError = "Ya no tienes intentos disponibles"
            }
        }
    }

    fun setImage(uri: Uri) {
        mediaError = ""
        viewModelScope.launch {
            try { form = form.copy(imagePath = repository.importImage(uri)) }
            catch (error: Exception) { mediaError = "No se pudo importar la imagen" }
        }
    }

    companion object {
        fun factory(repository: MPAZRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MPAZViewModel(repository) as T
        }
    }
}
