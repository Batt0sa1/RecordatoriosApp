package cl.ipp.recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme

enum class TipoEstado { Carga, Vacio, Error }

/**
 * Componente único para comunicar carga, vacío o error.
 * La acción (crear / reintentar) se comunica hacia arriba mediante una lambda.
 */
@Composable
fun EstadoPantalla(
    tipo: TipoEstado,
    mensaje: String,
    modifier: Modifier = Modifier,
    textoAccion: String? = null,
    onAccion: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (tipo) {
            TipoEstado.Carga -> CircularProgressIndicator()
            TipoEstado.Vacio -> Icon(Icons.Filled.Info, contentDescription = null, Modifier.size(48.dp))
            TipoEstado.Error -> Icon(
                Icons.Filled.Warning, contentDescription = "Error", Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        }
        Text(mensaje, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        if (textoAccion != null && onAccion != null) {
            Button(onClick = onAccion) { Text(textoAccion) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EstadoErrorPreview() {
    RecordatoriosTheme(dynamicColor = false) {
        EstadoPantalla(TipoEstado.Error, "No se pudieron cargar los recordatorios.", textoAccion = "Reintentar", onAccion = {})
    }
}
