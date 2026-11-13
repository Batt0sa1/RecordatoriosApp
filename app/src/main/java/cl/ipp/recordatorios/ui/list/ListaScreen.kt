package cl.ipp.recordatorios.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.EstadoRecordatorio
import cl.ipp.recordatorios.ui.components.EstadoPantalla
import cl.ipp.recordatorios.ui.components.RecordatorioCard
import cl.ipp.recordatorios.ui.components.TipoEstado
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme

/** Punto de entrada con estado: obtiene el ViewModel y observa su StateFlow respetando el ciclo de vida. */
@Composable
fun ListaScreen(
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (id: String) -> Unit,
    viewModel: ListaViewModel = viewModel(factory = ListaViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ListaContenido(
        uiState = uiState,
        onEvento = viewModel::onEvento,
        onCrearRecordatorio = onCrearRecordatorio,
        onRecordatorioClick = onRecordatorioClick,
    )
}

/** Versión sin estado: solo dibuja el UiState recibido y notifica acciones mediante lambdas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContenido(
    uiState: ListaUiState,
    onEvento: (ListaEvento) -> Unit,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Mis recordatorios") },
                actions = { MenuSimulacion(onEvento) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrearRecordatorio,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo") },
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            val contenidoModifier = Modifier.widthIn(max = 640.dp)
            // when exhaustivo: cada estado se dibuja de forma excluyente.
            when (uiState) {
                ListaUiState.Cargando -> EstadoPantalla(
                    TipoEstado.Carga, "Cargando recordatorios…", contenidoModifier,
                )
                ListaUiState.Vacio -> EstadoPantalla(
                    TipoEstado.Vacio, "Todavía no tienes recordatorios.\nCrea el primero para comenzar.",
                    contenidoModifier, textoAccion = "Crear recordatorio", onAccion = onCrearRecordatorio,
                )
                is ListaUiState.Error -> EstadoPantalla(
                    TipoEstado.Error, uiState.mensaje, contenidoModifier,
                    textoAccion = "Reintentar", onAccion = { onEvento(ListaEvento.Reintentar) },
                )
                is ListaUiState.ConDatos -> ListaConDatos(
                    uiState, onEvento, onRecordatorioClick, contenidoModifier,
                )
            }
        }
    }
}

@Composable
private fun ListaConDatos(
    estado: ListaUiState.ConDatos,
    onEvento: (ListaEvento) -> Unit,
    onRecordatorioClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = estado.filtro == null,
                        onClick = { onEvento(ListaEvento.FiltrarPorEstado(null)) },
                        label = { Text("Todos (${estado.recordatorios.size})") },
                    )
                }
                items(EstadoRecordatorio.todos) { e ->
                    FilterChip(
                        selected = estado.filtro == e,
                        onClick = { onEvento(ListaEvento.FiltrarPorEstado(e)) },
                        label = { Text("${e.nombre} (${estado.conteoPorEstado[e] ?: 0})") },
                    )
                }
            }
        }
        if (estado.visibles.isEmpty()) {
            item { Text("No hay recordatorios con este estado.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(estado.visibles, key = { it.id }) { recordatorio ->
            RecordatorioCard(recordatorio, onClick = onRecordatorioClick)
        }
    }
}

/** Menú para forzar los estados de la interfaz durante las pruebas. */
@Composable
private fun MenuSimulacion(onEvento: (ListaEvento) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    IconButton(onClick = { abierto = true }) {
        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones de simulación")
    }
    DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
        listOf(
            "Simular error de carga" to ListaEvento.SimularErrorCarga,
            "Simular error al guardar (próximo)" to ListaEvento.SimularErrorGuardado,
            "Vaciar recordatorios" to ListaEvento.VaciarDatos,
            "Restaurar ejemplos" to ListaEvento.RestaurarEjemplos,
        ).forEach { (texto, evento) ->
            DropdownMenuItem(text = { Text(texto) }, onClick = {
                abierto = false
                onEvento(evento)
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListaConDatosPreview() {
    RecordatoriosTheme(dynamicColor = false) {
        ListaContenido(ListaUiState.ConDatos(DatosSimulados.recordatorios), {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ListaVaciaPreview() {
    RecordatoriosTheme(dynamicColor = false) {
        ListaContenido(ListaUiState.Vacio, {}, {}, {})
    }
}
