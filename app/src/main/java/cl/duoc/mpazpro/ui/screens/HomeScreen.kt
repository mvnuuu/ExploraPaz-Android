package cl.duoc.mpazpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpro.ui.components.Heading
import cl.duoc.mpazpro.ui.components.Page

@Composable
fun HomeScreen(onUTP: () -> Unit, onTeacher: () -> Unit, onStudent: () -> Unit) {
    Page {
        Heading("¡Bienvenidos a MPAZ PRO!", "Apoyo didáctico para 2.º básico · prototipo académico")
        Text("Selecciona un perfil de prueba. Todos los usuarios son ficticios.")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Gestión pedagógica", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onUTP, Modifier.fillMaxWidth()) { Text("Entrar como UTP") }
                Text("Autoriza y publica unidades con actividades.")
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Docente DOC-001", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onTeacher, Modifier.fillMaxWidth()) { Text("Entrar como docente") }
                Text("Asigna unidades, configura intentos y consulta informes.")
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Estudiante EST-001", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onStudent, Modifier.fillMaxWidth()) { Text("Entrar como estudiante") }
                Text("Lee, practica y responde preguntas asignadas.")
            }
        }
        Text("Sin internet ni datos de alumnos reales. La selección de perfil no equivale a autenticación segura.",
            style = MaterialTheme.typography.bodySmall)
    }
}
