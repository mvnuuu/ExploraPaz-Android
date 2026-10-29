package cl.duoc.mpazpro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpro.model.*
import cl.duoc.mpazpro.ui.components.*
import cl.duoc.mpazpro.viewmodel.MPAZViewModel

@Composable
fun StudentScreen(state: AppState, onLesson: (Int) -> Unit) {
    val units = state.units.filter { it.status == UnitStatus.PUBLICADO && state.assignment(it.id) != null }
    Page {
        Heading("¡Hola, estudiante!", "EST-001 · Aprende a tu ritmo")
        Text("Mis actividades", style = MaterialTheme.typography.titleLarge)
        if (units.isEmpty()) Text("Tu docente todavía no ha asignado actividades. Vuelve más tarde.")
        units.forEach { unit ->
            UnitCard(unit) {
                val progress = state.progress(unit.id)
                Text(if (progress.read) "Contenido revisado" else "Sin comenzar")
                Button(onClick = { onLesson(unit.id) }, Modifier.fillMaxWidth()) { Text("Aprender y practicar") }
            }
        }
        Text("Pide ayuda a tu profesor cuando la necesites.")
    }
}

@Composable
fun LessonScreen(unit: DidacticUnit?, state: AppState, vm: MPAZViewModel, onQuiz: () -> Unit) {
    if (unit == null || !vm.canStudentOpen(unit.id)) {
        Page { Text("Este contenido no está disponible para tu perfil.") }
        return
    }
    val progress = state.progress(unit.id)
    val assignment = state.assignment(unit.id)!!
    Page {
        Heading(unit.title, "${unit.subject.label} · ${unit.objectiveCode}")
        Text("Nuestro objetivo", style = MaterialTheme.typography.titleMedium)
        Text(unit.objective, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider()
        Text("Leamos juntos", style = MaterialTheme.typography.titleMedium)
        Text(unit.explanation, style = MaterialTheme.typography.bodyLarge)
        ImagePreview(unit.imagePath)
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ejemplo guiado", style = MaterialTheme.typography.titleMedium)
                Text(unit.example, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (!progress.read) {
            Button(onClick = { vm.markRead(unit.id) }, Modifier.fillMaxWidth()) { Text("Ya leí y comprendí") }
        }
        AnimatedVisibility(visible = progress.read) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("¡Muy bien! Terminaste la lectura.", fontWeight = FontWeight.SemiBold)
                Text("Intentos disponibles: ${assignment.maxAttempts - progress.attempts}")
                Button(onClick = onQuiz, enabled = vm.canAttempt(unit.id), modifier = Modifier.fillMaxWidth()) {
                    Text(if (vm.canAttempt(unit.id)) "Responder desafío final" else "Ya usaste tus intentos")
                }
            }
        }
    }
}

@Composable
fun QuizScreen(unit: DidacticUnit?, vm: MPAZViewModel, onFinished: () -> Unit) {
    if (unit == null || !vm.canAttempt(unit.id)) {
        Page { Heading("Prueba no disponible", "Debes completar la lectura y tener intentos disponibles.") }
        return
    }
    var selected by remember(unit.id) { mutableIntStateOf(-1) }
    Page {
        Heading("Desafío final", "Lee la pregunta y elige una respuesta.")
        Text(unit.question, style = MaterialTheme.typography.headlineSmall)
        unit.choices.forEachIndexed { index, choice ->
            Card(onClick = { selected = index; vm.clearQuizError() }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(10.dp)) {
                    RadioButton(selected = selected == index, onClick = { selected = index; vm.clearQuizError() })
                    TextButton(onClick = { selected = index; vm.clearQuizError() }) {
                        Text("${'A' + index}. $choice", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        if (vm.quizError.isNotBlank()) Text(vm.quizError, color = MaterialTheme.colorScheme.error)
        Button(onClick = { vm.submit(unit.id, selected, onFinished) }, modifier = Modifier.fillMaxWidth()) { Text("Enviar respuesta") }
    }
}

@Composable
fun ResultScreen(unit: DidacticUnit?, state: AppState, onBackToStudent: () -> Unit) {
    if (unit == null) { Page { Text("Resultado no disponible") }; return }
    val p = state.progress(unit.id)
    val isCorrect = p.lastChoice == unit.correctChoice
    Page {
        Heading(if (isCorrect) "¡Respuesta correcta!" else "¡Sigue practicando!",
            "Esta es la retroalimentación de tu desafío.")
        Text("Tu respuesta: ${unit.choices.getOrNull(p.lastChoice) ?: "Sin respuesta"}")
        Text("Respuesta correcta: ${unit.choices[unit.correctChoice]}", fontWeight = FontWeight.SemiBold)
        Text("${p.attempts} intento(s) realizados")
        Button(onClick = onBackToStudent, Modifier.fillMaxWidth()) { Text("Volver a mis actividades") }
    }
}
