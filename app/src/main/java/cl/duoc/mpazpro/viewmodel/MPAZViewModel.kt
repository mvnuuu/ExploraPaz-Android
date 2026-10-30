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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Toda validación y toda operación con datos se gestiona fuera de los composables. */
class MPAZViewModel(private val repository: MPAZRepository) : ViewModel() {
    val state = repository.state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppState())

    var form by mutableStateOf(UnitForm())
        private set
    var errors by mutableStateOf(UnitErrors())
        private set
    var assignmentLimit by mutableStateOf("2")
        private set
    var assignmentError by mutableStateOf("")
        private set
    var mediaError by mutableStateOf("")
        private set
    var quizError by mutableStateOf("")
        private set

    fun beginNew() { form = UnitForm(); errors = UnitErrors(); mediaError = "" }
    fun beginEdit(unit: DidacticUnit) { form = UnitForm.from(unit); errors = UnitErrors(); mediaError = "" }
    fun updateForm(newValue: UnitForm) { form = newValue; errors = UnitErrors() }
    fun updateLimit(text: String) { assignmentLimit = text; assignmentError = "" }

    fun validateUnit(): Boolean {
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
        viewModelScope.launch { repository.saveUnit(unit); onSuccess() }
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
        if (assignmentError.isNotEmpty() || count == null) return
        viewModelScope.launch { repository.assign(unit.id, count); onSuccess() }
    }

    fun canStudentOpen(id: Int): Boolean = state.value.units.any { it.id == id && it.status == UnitStatus.PUBLICADO }
        && state.value.assignment(id) != null

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
