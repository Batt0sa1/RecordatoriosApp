package cl.ipp.recordatorios.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.Recordatorio
import cl.ipp.recordatorios.ui.components.CategoriaChip
import cl.ipp.recordatorios.ui.components.EstadoPantalla
import cl.ipp.recordatorios.ui.components.TipoEstado
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme
import java.time.format.DateTimeFormatter

@Composable
fun DetalleScreen(
    onVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel(factory = DetalleViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetalleContenido(uiState, onVolver = onVolver, onReintentar = viewModel::reintentar)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContenido(
    uiState: DetalleUiState,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.TopCenter) {
            val m = Modifier.widthIn(max = 640.dp)
            when (uiState) {
                DetalleUiState.Cargando -> EstadoPantalla(TipoEstado.Carga, "Cargando detalle…", m)
                DetalleUiState.NoEncontrado -> EstadoPantalla(
                    TipoEstado.Vacio, "El recordatorio no existe o fue eliminado.", m,
                    textoAccion = "Volver", onAccion = onVolver,
                )
                is DetalleUiState.Error -> EstadoPantalla(
                    TipoEstado.Error, uiState.mensaje, m, textoAccion = "Reintentar", onAccion = onReintentar,
                )
                is DetalleUiState.Encontrado -> DetalleRecordatorio(uiState.recordatorio, m)
            }
        }
    }
}

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
private fun DetalleRecordatorio(r: Recordatorio, modifier: Modifier = Modifier) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(r.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        CategoriaChip(r.categoria)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilaDato("Descripción", r.descripcion ?: "Sin descripción")
                HorizontalDivider()
                FilaDato("Categoría", r.categoria.nombre)
                FilaDato("Prioridad", r.prioridad.nombre)
                FilaDato("Estado", r.estado.nombre)
                FilaDato("Fecha límite", r.fechaLimite?.format(formatoFecha) ?: "Sin fecha")
                FilaDato("Ubicación", r.ubicacion ?: "Sin ubicación")
                HorizontalDivider()
                FilaDato("Identificador", r.id)
            }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun DetallePreview() {
    RecordatoriosTheme(dynamicColor = false) {
        DetalleContenido(DetalleUiState.Encontrado(DatosSimulados.recordatorios[1]), {}, {})
    }
}
