package cl.ipp.recordatorios.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.ipp.recordatorios.RecordatoriosApplication
import cl.ipp.recordatorios.data.RecordatorioRepository
import cl.ipp.recordatorios.model.Validador
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CrearViewModel(
    private val repositorio: RecordatorioRepository,
    private val generarId: () -> String = { UUID.randomUUID().toString() },
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearUiState())
    val uiState: StateFlow<CrearUiState> = _uiState.asStateFlow()

    // Los errores de título, categoría y prioridad se muestran recién cuando la persona
    // comenzó a escribir el título, para no mostrar un formulario "lleno de errores" al abrirlo.
    private var tituloTocado = false

    fun onEvento(evento: CrearEvento) {
        if (_uiState.value.enviando) return // No se aceptan cambios mientras hay un envío en curso.
        when (evento) {
            is CrearEvento.TituloCambiado -> {
                tituloTocado = true
                actualizarYValidar { it.copy(titulo = evento.valor) }
            }
            is CrearEvento.DescripcionCambiada -> actualizarYValidar { it.copy(descripcion = evento.valor) }
            is CrearEvento.CategoriaSeleccionada -> actualizarYValidar { it.copy(categoria = evento.categoria) }
            is CrearEvento.PrioridadSeleccionada -> actualizarYValidar { it.copy(prioridad = evento.prioridad) }
            CrearEvento.DescartarError -> _uiState.update { it.copy(envio = EstadoEnvio.Inactivo) }
            CrearEvento.Guardar -> guardar()
        }
    }

    /** Aplica el cambio y recalcula las validaciones produciendo una nueva instancia inmutable. */
    private fun actualizarYValidar(cambio: (CrearUiState) -> CrearUiState) {
        _uiState.update { anterior -> validar(cambio(anterior)) }
    }

    private fun validar(s: CrearUiState): CrearUiState {
        val eTitulo = Validador.validarTitulo(s.titulo)
        val eDescripcion = Validador.validarDescripcion(s.descripcion)
        val eCategoria = Validador.validarCategoria(s.categoria)
        val ePrioridad = Validador.validarPrioridad(s.prioridad)
        return s.copy(
            errorTitulo = eTitulo?.mensaje?.takeIf { tituloTocado },
            errorDescripcion = eDescripcion?.mensaje,
            errorCategoria = eCategoria?.mensaje?.takeIf { tituloTocado },
            errorPrioridad = ePrioridad?.mensaje?.takeIf { tituloTocado },
            esValido = listOfNotNull(eTitulo, eDescripcion, eCategoria, ePrioridad).isEmpty(),
            // Si la persona edita después de un error, el mensaje general deja de mostrarse.
            envio = if (s.envio is EstadoEnvio.Error) EstadoEnvio.Inactivo else s.envio,
        )
    }

    private fun guardar() {
        val actual = _uiState.value
        if (!actual.puedeGuardar) return

        val resultado = Validador.crearRecordatorio(
            id = generarId(),
            titulo = actual.titulo,
            descripcion = actual.descripcion,
            categoria = actual.categoria,
            prioridad = actual.prioridad,
        )
        val recordatorio = resultado.getOrElse { error ->
            _uiState.update { it.copy(envio = EstadoEnvio.Error(error.message ?: "Datos inválidos.")) }
            return
        }

        _uiState.update { it.copy(envio = EstadoEnvio.Enviando) }
        viewModelScope.launch {
            try {
                val guardado = repositorio.guardar(recordatorio)
                _uiState.update { it.copy(envio = EstadoEnvio.Exito(guardado.id)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(envio = EstadoEnvio.Error(e.message ?: "No se pudo guardar el recordatorio.")) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as RecordatoriosApplication).container
                CrearViewModel(container.repositorio)
            }
        }
    }
}
