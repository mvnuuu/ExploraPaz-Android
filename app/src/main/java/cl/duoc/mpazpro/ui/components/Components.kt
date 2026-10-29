package cl.duoc.mpazpro.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpro.model.DidacticUnit

@Composable
fun Page(content: @Composable ColumnScope.() -> Unit) {
    // Ancho moderado en tablets; desplazamiento vertical para teléfonos.
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp)
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp), content = content
        )
    }
}

@Composable
fun Heading(title: String, subtitle: String) {
    Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun UnitCard(unit: DidacticUnit, footer: @Composable ColumnScope.() -> Unit = {}) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${unit.subject.emoji}  ${unit.subject.label}", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
            Text(unit.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(unit.objectiveCode + " · " + unit.status.label, style = MaterialTheme.typography.labelMedium)
            Text(unit.objective, style = MaterialTheme.typography.bodyMedium)
            footer()
        }
    }
}

@Composable
fun ValidationField(
    value: String, label: String, error: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier, singleLine: Boolean = true
) {
    OutlinedTextField(value = value, onValueChange = onValueChange,
        label = { Text(label) }, modifier = modifier.fillMaxWidth(),
        isError = error.isNotEmpty(), singleLine = singleLine,
        trailingIcon = {
            Icon(if (error.isNotEmpty() || value.isBlank()) Icons.Outlined.Edit else Icons.Outlined.CheckCircle,
                contentDescription = if (error.isNotEmpty()) "Revisar campo" else "Entrada")
        },
        supportingText = if (error.isNotEmpty()) ({ Text(error) }) else null
    )
}

@Composable
fun ImagePreview(path: String) {
    if (path.isBlank()) return
    val bitmap = remember(path) {
        BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = 4 })?.asImageBitmap()
    }
    if (bitmap != null) {
        Image(bitmap, contentDescription = "Imagen didáctica de apoyo",
            modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp))
    } else Text("Imagen no disponible en este dispositivo", color = MaterialTheme.colorScheme.error)
}

@Composable
fun AnimatedSuccess(visible: Boolean, message: String) {
    val color by animateColorAsState(
        if (visible) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        label = "feedback de guardado"
    )
    AnimatedVisibility(visible = visible) {
        Box(Modifier.fillMaxWidth().background(color, RoundedCornerShape(12.dp)).padding(14.dp)) {
            Text(message, fontWeight = FontWeight.Medium)
        }
    }
}
