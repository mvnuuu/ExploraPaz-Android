package cl.duoc.mpazpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpro.model.*
import cl.duoc.mpazpro.ui.components.*
import cl.duoc.mpazpro.viewmodel.MPAZViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeacherScreen(state: AppState, vm: MPAZViewModel, onReports: () -> Unit) {
    var chosenId by remember { mutableIntStateOf(0) }
    var savedId by remember { mutableIntStateOf(0) }
    val available = state.units.filter { it.status == UnitStatus.PUBLICADO }
    Page {
        Heading("Panel docente", "Curso de prueba: 2.º básico A · Estudiante EST-001")
        Button(onClick = onReports, Modifier.fillMaxWidth()) { Text("Ver informes pedagógicos") }
        Text("Unidades publicadas y disponibles", style = MaterialTheme.typography.titleLarge)
        if (available.isEmpty()) {
            Text("Aún no hay unidades publicadas por UTP.")
        }
        available.forEach { unit ->
            UnitCard(unit) {
                val assignment = state.assignment(unit.id)
                if (assignment != null) Text("Asignada · máximo ${assignment.maxAttempts} intento(s)")
                OutlinedButton(onClick = {
                    chosenId = unit.id
                    savedId = 0
                    vm.updateLimit((assignment?.maxAttempts ?: 2).toString())
                }, Modifier.fillMaxWidth()) {
                    Text(if (assignment == null) "Configurar asignación" else "Cambiar límite de intentos")
                }
                if (chosenId == unit.id) {
                    OutlinedTextField(
                        value = vm.assignmentLimit, onValueChange = vm::updateLimit,
                        label = { Text("Intentos permitidos (1 a 5)") },
                        modifier = Modifier.fillMaxWidth(), isError = vm.assignmentError.isNotBlank(),
                        supportingText = if (vm.assignmentError.isNotBlank()) ({ Text(vm.assignmentError) }) else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        trailingIcon = { Icon(Icons.Outlined.Edit, contentDescription = "Intentos") }
                    )
                    Button(onClick = { vm.assign(unit) { savedId = unit.id; chosenId = 0 } },
                        Modifier.fillMaxWidth()) { Text("Asignar a EST-001") }
                }
                AnimatedSuccess(savedId == unit.id, "Asignación guardada localmente")
            }
        }
        Text("Los cambios se guardan en este dispositivo. El prototipo no sincroniza varios teléfonos.",
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ReportScreen(state: AppState) {
    Page {
        Heading("Informe pedagógico", "Acceso desde perfil docente · registros ficticios")
        Text("Estudiante EST-001 · Curso 2.º básico A", style = MaterialTheme.typography.titleMedium)
        val units = state.units.filter { state.assignment(it.id) != null }
        if (units.isEmpty()) Text("No hay unidades asignadas para mostrar informes.")
        units.forEach { unit ->
            val p = state.progress(unit.id)
            val assignment = state.assignment(unit.id)!!
            val date = if (p.lastEpoch > 0L) SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL"))
                .format(Date(p.lastEpoch)) else "Sin respuestas"
            val percentage = if (p.attempts == 0) 0 else p.correctCount * 100 / p.attempts
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${unit.subject.label} · ${unit.objectiveCode}", style = MaterialTheme.typography.titleMedium)
                    Text(unit.title)
                    Text("Lectura completada: ${if (p.read) "Sí" else "No"}")
                    Text("Intentos: ${p.attempts} / ${assignment.maxAttempts}")
                    Text("Aciertos: ${p.correctCount} de ${p.attempts} · $percentage%")
                    LinearProgressIndicator(progress = { percentage / 100f }, Modifier.fillMaxWidth())
                    Text("Última respuesta: $date")
                    Text("Interpretación y observaciones pedagógicas: a cargo del docente")
                }
            }
        }
        Text("Los resultados son descriptivos; ninguna observación sustituye el diagnóstico docente.",
            style = MaterialTheme.typography.bodySmall)
    }
}
