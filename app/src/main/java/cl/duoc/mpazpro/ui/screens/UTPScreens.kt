package cl.duoc.mpazpro.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import cl.duoc.mpazpro.model.*
import cl.duoc.mpazpro.ui.components.*
import cl.duoc.mpazpro.viewmodel.MPAZViewModel
import java.io.File

@Composable
fun UTPScreen(state: AppState, vm: MPAZViewModel, onEdit: (Int) -> Unit, onNew: () -> Unit) {
    Page {
        Heading("Administración UTP", "Autoriza los contenidos antes de que el docente pueda asignarlos.")
        Button(onClick = { vm.beginNew(); onNew() }, Modifier.fillMaxWidth()) { Text("Crear unidad didáctica") }
        state.units.forEach { unit ->
            UnitCard(unit) {
                OutlinedButton(onClick = { vm.beginEdit(unit); onEdit(unit.id) }, Modifier.fillMaxWidth()) {
                    Text("Editar contenido")
                }
                when (unit.status) {
                    UnitStatus.BORRADOR -> Button(onClick = { vm.authorize(unit) }, Modifier.fillMaxWidth()) {
                        Text("Autorizar unidad")
                    }
                    UnitStatus.AUTORIZADO -> Button(onClick = { vm.publish(unit) }, Modifier.fillMaxWidth()) {
                        Text("Publicar unidad")
                    }
                    UnitStatus.PUBLICADO -> OutlinedButton(onClick = { vm.disable(unit) }, Modifier.fillMaxWidth()) {
                        Text("Deshabilitar")
                    }
                    UnitStatus.DESHABILITADO -> OutlinedButton(onClick = { vm.authorizeDisabled(unit) }, Modifier.fillMaxWidth()) {
                        Text("Regresar a borrador")
                    }
                }
            }
        }
    }
}

@Composable
fun UnitFormScreen(vm: MPAZViewModel, onSaved: () -> Unit) {
    val f = vm.form
    val e = vm.errors
    Page {
        Heading(if (f.id == 0) "Nueva unidad" else "Editar unidad",
            "Formulario UTP. Guardar modificaciones devuelve la unidad a borrador.")
        Text("Asignatura", style = MaterialTheme.typography.titleMedium)
        Subject.values().forEach { subject ->
            Row(Modifier.fillMaxWidth()) {
                RadioButton(selected = f.subject == subject, onClick = { vm.updateForm(f.copy(subject = subject)) })
                TextButton(onClick = { vm.updateForm(f.copy(subject = subject)) }) { Text(subject.label) }
            }
        }
        ValidationField(f.title, "Título", e.title, { vm.updateForm(f.copy(title = it)) })
        ValidationField(f.objectiveCode, "Código OA", e.code, { vm.updateForm(f.copy(objectiveCode = it)) })
        ValidationField(f.objective, "Objetivo de aprendizaje", e.objective,
            { vm.updateForm(f.copy(objective = it)) }, singleLine = false)
        ValidationField(f.explanation, "Explicación", e.explanation,
            { vm.updateForm(f.copy(explanation = it)) }, singleLine = false)
        ValidationField(f.example, "Ejemplo guiado", e.example,
            { vm.updateForm(f.copy(example = it)) }, singleLine = false)
        ValidationField(f.question, "Pregunta final", e.question,
            { vm.updateForm(f.copy(question = it)) }, singleLine = false)
        Text("Alternativas (cuatro obligatorias)", style = MaterialTheme.typography.titleMedium)
        ValidationField(f.choice1, "A", e.choices, { vm.updateForm(f.copy(choice1 = it)) })
        ValidationField(f.choice2, "B", e.choices, { vm.updateForm(f.copy(choice2 = it)) })
        ValidationField(f.choice3, "C", e.choices, { vm.updateForm(f.copy(choice3 = it)) })
        ValidationField(f.choice4, "D", e.choices, { vm.updateForm(f.copy(choice4 = it)) })
        Text("Respuesta correcta", style = MaterialTheme.typography.titleMedium)
        (0..3).forEach { index ->
            Row {
                RadioButton(selected = index == f.correctChoice,
                    onClick = { vm.updateForm(f.copy(correctChoice = index)) })
                TextButton(onClick = { vm.updateForm(f.copy(correctChoice = index)) }) {
                    Text("Alternativa ${'A' + index}")
                }
            }
        }
        Text("Imagen educativa original o autorizada (opcional)", style = MaterialTheme.typography.titleMedium)
        NativeMediaPicker(vm)
        ImagePreview(f.imagePath)
        if (vm.mediaError.isNotBlank()) Text(vm.mediaError, color = MaterialTheme.colorScheme.error)
        Button(onClick = { vm.saveUnit(onSaved) }, Modifier.fillMaxWidth()) { Text("Guardar unidad") }
        Text("Si hay errores, el formulario mostrará mensajes por campo.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun NativeMediaPicker(vm: MPAZViewModel) {
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) vm.setImage(uri)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        if (saved) cameraUri?.let(vm::setImage)
    }
    OutlinedButton(onClick = { galleryLauncher.launch("image/*") }, Modifier.fillMaxWidth()) {
        Text("Seleccionar desde galería")
    }
    OutlinedButton(onClick = {
        val tmp = File.createTempFile("mpaz_foto_", ".jpg", context.cacheDir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tmp)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }, Modifier.fillMaxWidth()) {
        Text("Fotografiar material didáctico")
    }
    Text("Los recursos cámara y galería son opcionales y están disponibles solo en el perfil UTP.",
        style = MaterialTheme.typography.bodySmall)
}
