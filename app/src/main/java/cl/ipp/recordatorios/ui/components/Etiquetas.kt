package cl.ipp.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.Categoria

/** Etiqueta de categoría: usa el texto y el color asociados en el Map de la Actividad 1. */
@Composable
fun CategoriaChip(categoria: Categoria, modifier: Modifier = Modifier) {
    val etiqueta = DatosSimulados.etiquetaDe(categoria)
    val color = Color(etiqueta.colorArgb)
    Text(
        etiqueta.texto,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
fun Etiqueta(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}
