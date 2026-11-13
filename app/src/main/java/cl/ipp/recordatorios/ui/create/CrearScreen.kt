package cl.ipp.recordatorios.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.ipp.recordatorios.model.Categoria
import cl.ipp.recordatorios.model.DESCRIPCION_MAX_CARACTERES
import cl.ipp.recordatorios.ui.components.CampoTextoValidado
import cl.ipp.recordatorios.ui.components.SelectorCategoria
import cl.ipp.recordatorios.ui.components.SelectorPrioridad
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme

@Composable
fun CrearScreen(
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    viewModel: CrearViewModel = viewModel(factory = CrearViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando el ViewModel informa éxito, se navega de regreso (una sola vez por id creado).
    val exito = uiState.envio as? EstadoEnvio.Exito
    LaunchedEffect(exito) {
        if (exito != null) onGuardar()
    }

    CrearContenido(uiState = uiState, onEvento = viewModel::onEvento, onCancelar = onCancelar)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearContenido(
    uiState: CrearUiState,
    onEvento: (CrearEvento) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val habilitado = !uiState.enviando
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Nuevo recordatorio") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar y volver")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CampoTextoValidado(
                    valor = uiState.titulo,
                    onValorChange = { onEvento(CrearEvento.TituloCambiado(it)) },
                    etiqueta = "Título *",
                    error = uiState.errorTitulo,
                    habilitado = habilitado,
                )
                CampoTextoValidado(
                    valor = uiState.descripcion,
                    onValorChange = { onEvento(CrearEvento.DescripcionCambiada(it)) },
                    etiqueta = "Descripción (opcional)",
                    error = uiState.errorDescripcion,
                    maxCaracteres = DESCRIPCION_MAX_CARACTERES,
                    singleLine = false,
                    habilitado = habilitado,
                )
                SelectorCategoria(
                    seleccionada = uiState.categoria,
                    onSeleccion = { onEvento(CrearEvento.CategoriaSeleccionada(it)) },
                    error = uiState.errorCategoria,
                    opciones = Categoria.todas,
                    habilitado = habilitado,
                )
                SelectorPrioridad(
                    seleccionada = uiState.prioridad,
                    onSeleccion = { onEvento(CrearEvento.PrioridadSeleccionada(it)) },
                    error = uiState.errorPrioridad,
                    habilitado = habilitado,
                )

                (uiState.envio as? EstadoEnvio.Error)?.let { error ->
                    ErrorGuardado(
                        mensaje = error.mensaje,
                        onReintentar = { onEvento(CrearEvento.Guardar) },
                        onDescartar = { onEvento(CrearEvento.DescartarError) },
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                ) {
                    OutlinedButton(onClick = onCancelar, enabled = habilitado) { Text("Cancelar") }
                    Button(onClick = { onEvento(CrearEvento.Guardar) }, enabled = uiState.puedeGuardar) {
                        if (uiState.enviando) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text("  Guardando…")
                        } else {
                            Text("Guardar")
                        }
                    }
                }
                if (!uiState.esValido) {
                    Text(
                        "Completa los campos obligatorios (*), la categoría y la prioridad para habilitar \"Guardar\".",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

/** Error general de guardado con opción de reintento. */
@Composable
private fun ErrorGuardado(
    mensaje: String,
    onReintentar: () -> Unit,
    onDescartar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Assertive },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Warning, contentDescription = "Error")
                Text(mensaje, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDescartar) { Text("Cerrar") }
                TextButton(onClick = onReintentar) { Text("Reintentar") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CrearContenidoPreview() {
    RecordatoriosTheme(dynamicColor = false) {
        CrearContenido(
            CrearUiState(titulo = "ab", errorTitulo = "El título debe tener al menos 4 caracteres."),
            onEvento = {}, onCancelar = {},
        )
    }
}
