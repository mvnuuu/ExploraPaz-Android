package cl.duoc.mpazpro.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import cl.duoc.mpazpro.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

private val Context.mpazDataStore by preferencesDataStore(name = "mpaz_ep2")

/** Persistencia local basada en DataStore Preferences, según Guía 12 (EA2). */
class MPAZRepository(private val context: Context) {
    private val store = context.mpazDataStore
    private val idsKey = stringPreferencesKey("unit_ids")
    private fun s(id: Int, name: String) = stringPreferencesKey("unit_${id}_$name")
    private fun i(id: Int, name: String) = intPreferencesKey("unit_${id}_$name")
    private fun b(id: Int, name: String) = booleanPreferencesKey("unit_${id}_$name")
    private fun l(id: Int, name: String) = longPreferencesKey("unit_${id}_$name")

    val state: Flow<AppState> = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map { p ->
        val extraIds = p[idsKey]?.split(",")?.mapNotNull { it.toIntOrNull() }.orEmpty()
        val defaultIds = DemoContent.units.map { it.id }
        val ids = (defaultIds + extraIds).distinct()
        val units = ids.map { id ->
            val base = DemoContent.units.firstOrNull { it.id == id }
            DidacticUnit(
                id = id,
                subject = Subject.values().firstOrNull { it.name == p[s(id, "subject")] }
                    ?: base?.subject ?: Subject.LENGUAJE,
                title = p[s(id, "title")] ?: base?.title.orEmpty(),
                objectiveCode = p[s(id, "code")] ?: base?.objectiveCode.orEmpty(),
                objective = p[s(id, "objective")] ?: base?.objective.orEmpty(),
                explanation = p[s(id, "explanation")] ?: base?.explanation.orEmpty(),
                example = p[s(id, "example")] ?: base?.example.orEmpty(),
                question = p[s(id, "question")] ?: base?.question.orEmpty(),
                choices = (0..3).map { index ->
                    p[s(id, "choice_$index")] ?: base?.choices?.get(index).orEmpty()
                },
                correctChoice = p[i(id, "answer")] ?: base?.correctChoice ?: 0,
                imagePath = p[s(id, "image")] ?: base?.imagePath.orEmpty(),
                status = UnitStatus.values().firstOrNull { it.name == p[s(id, "status")] }
                    ?: base?.status ?: UnitStatus.BORRADOR
            )
        }
        AppState(
            loaded = true,
            units = units,
            assignments = ids.map { id ->
                Assignment(id, p[i(id, "max_attempts")] ?: 2, p[b(id, "assigned")] ?: false)
            },
            progress = ids.map { id ->
                Progress(id, p[b(id, "read")] ?: false, p[i(id, "attempts")] ?: 0,
                    p[i(id, "correct_count")] ?: 0, p[i(id, "last_choice")] ?: -1,
                    p[l(id, "last_epoch")] ?: 0L)
            }
        )
    }

    suspend fun saveUnit(unit: DidacticUnit) {
        store.edit { p ->
            val known = p[idsKey]?.split(",")?.mapNotNull { it.toIntOrNull() }.orEmpty()
            if (unit.id !in DemoContent.units.map { it.id } && unit.id !in known) {
                p[idsKey] = (known + unit.id).joinToString(",")
            }
            p[s(unit.id, "subject")] = unit.subject.name
            p[s(unit.id, "title")] = unit.title
            p[s(unit.id, "code")] = unit.objectiveCode
            p[s(unit.id, "objective")] = unit.objective
            p[s(unit.id, "explanation")] = unit.explanation
            p[s(unit.id, "example")] = unit.example
            p[s(unit.id, "question")] = unit.question
            unit.choices.forEachIndexed { index, choice -> p[s(unit.id, "choice_$index")] = choice }
            p[i(unit.id, "answer")] = unit.correctChoice
            p[s(unit.id, "image")] = unit.imagePath
            p[s(unit.id, "status")] = unit.status.name
        }
    }

    suspend fun changeStatus(id: Int, status: UnitStatus) {
        store.edit { p -> p[s(id, "status")] = status.name }
    }

    suspend fun assign(id: Int, attempts: Int) {
        store.edit { p ->
            p[b(id, "assigned")] = true
            p[i(id, "max_attempts")] = attempts
        }
    }

    suspend fun markRead(id: Int) {
        store.edit { p -> p[b(id, "read")] = true }
    }

    suspend fun recordAttempt(id: Int, choice: Int, correct: Boolean): Boolean {
        // La comprobación se ejecuta dentro de DataStore.edit (atómica) para impedir
        // superar el máximo de intentos incluso ante varios toques rápidos.
        var saved = false
        store.edit { p ->
            val allowed = p[b(id, "assigned")] == true &&
                p[s(id, "status")] == UnitStatus.PUBLICADO.name &&
                p[b(id, "read")] == true
            val used = p[i(id, "attempts")] ?: 0
            val limit = p[i(id, "max_attempts")] ?: 2
            if (allowed && choice in 0..3 && used < limit) {
                p[i(id, "attempts")] = used + 1
                if (correct) p[i(id, "correct_count")] = (p[i(id, "correct_count")] ?: 0) + 1
                p[i(id, "last_choice")] = choice
                p[l(id, "last_epoch")] = System.currentTimeMillis()
                saved = true
            }
        }
        return saved
    }

    // Copia a almacenamiento privado: URI de galería/cámara no queda ligada a un permiso temporal.
    suspend fun importImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val destinationDir = File(context.filesDir, "material_visual").apply { mkdirs() }
        val destination = File.createTempFile("didactico_", ".jpg", destinationDir)
        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw IOException("No se pudo abrir la imagen")
            input.use { stream -> destination.outputStream().use { output -> stream.copyTo(output) } }
            destination.absolutePath
        } catch (error: Exception) {
            destination.delete()
            throw error
        }
    }
}
