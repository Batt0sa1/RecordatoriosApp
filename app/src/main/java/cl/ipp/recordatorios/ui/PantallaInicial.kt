package cl.ipp.recordatorios.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.EstadoRecordatorio
import cl.ipp.recordatorios.model.Prioridad
import cl.ipp.recordatorios.model.Recordatorio
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme

/**
 * Pantalla inicial (Actividad 1): muestra los recordatorios simulados con
 * título, categoría, prioridad y estado, más un resumen y filtros por estado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicial(
    recordatorios: List<Recordatorio>,
    modifier: Modifier = Modifier,
) {
    // Filtro local de la pantalla: null = todos los estados.
    var filtroEstado by remember { mutableStateOf<EstadoRecordatorio?>(null) }
    val visibles = DatosSimulados.ordenarPorPrioridad(
        filtroEstado?.let { DatosSimulados.filtrarPorEstado(it, recordatorios) } ?: recordatorios
    )
    val conteo = DatosSimulados.conteoPorEstado(recordatorios)

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Mis recordatorios") }) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "${recordatorios.size} recordatorios · ${DatosSimulados.categoriasUsadas(recordatorios).size} categorías · " +
                        "${DatosSimulados.filtrarPorPrioridad(Prioridad.Alta, recordatorios).size} de prioridad alta",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filtroEstado == null,
                            onClick = { filtroEstado = null },
                            label = { Text("Todos (${recordatorios.size})") },
                        )
                    }
                    items(EstadoRecordatorio.todos) { estado ->
                        FilterChip(
                            selected = filtroEstado == estado,
                            onClick = { filtroEstado = estado },
                            label = { Text("${estado.nombre} (${conteo[estado] ?: 0})") },
                        )
                    }
                }
            }
            if (visibles.isEmpty()) {
                item { Text("No hay recordatorios con este estado.") }
            }
            items(visibles, key = { it.id }) { recordatorio ->
                TarjetaRecordatorio(recordatorio)
            }
        }
    }
}

@Composable
fun TarjetaRecordatorio(recordatorio: Recordatorio, modifier: Modifier = Modifier) {
    val etiqueta = DatosSimulados.etiquetaDe(recordatorio.categoria)
    Card(modifier = modifier.fillMaxWidth()) {
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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                AssistChip(
                    onClick = {},
                    label = { Text(etiqueta.texto) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(etiqueta.colorArgb).copy(alpha = 0.15f),
                        labelColor = Color(etiqueta.colorArgb),
                    ),
                )
                Spacer(Modifier.width(8.dp))
                Text("Prioridad: ${recordatorio.prioridad.nombre}", style = MaterialTheme.typography.labelMedium)
            }
            Text("Estado: ${recordatorio.estado.nombre}", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaInicialPreview() {
    RecordatoriosTheme(dynamicColor = false) {
        PantallaInicial(DatosSimulados.recordatorios)
    }
}
