package cl.ipp.recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.Recordatorio
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme

/** Componente reutilizable que representa un recordatorio en una lista. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecordatorioCard(
    recordatorio: Recordatorio,
    onClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onClick(recordatorio.id) },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${recordatorio.titulo}. Categoría ${recordatorio.categoria.nombre}, " +
                    "prioridad ${recordatorio.prioridad.nombre}, estado ${recordatorio.estado.nombre}"
            },
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            recordatorio.descripcion?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CategoriaChip(recordatorio.categoria)
                Etiqueta("Prioridad: ${recordatorio.prioridad.nombre}")
                Etiqueta("Estado: ${recordatorio.estado.nombre}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordatorioCardPreview() {
    RecordatoriosTheme(dynamicColor = false) {
        RecordatorioCard(DatosSimulados.recordatorios.first(), onClick = {})
    }
}
