package cl.ipp.recordatorios.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.ipp.recordatorios.RecordatoriosApplication
import cl.ipp.recordatorios.data.RecordatorioRepository
import cl.ipp.recordatorios.data.SimuladorDatos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListaViewModel(
    private val repositorio: RecordatorioRepository,
    private val simulador: SimuladorDatos,
) : ViewModel() {

    // Estado modificable solo dentro del ViewModel…
    private val _uiState = MutableStateFlow<ListaUiState>(ListaUiState.Cargando)
    // …y expuesto a la interfaz como StateFlow de solo lectura.
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    private var cargaJob: Job? = null

    init {
        cargar()
    }

    fun onEvento(evento: ListaEvento) {
        when (evento) {
            ListaEvento.Reintentar -> cargar()
            is ListaEvento.FiltrarPorEstado -> _uiState.update { estado ->
                if (estado is ListaUiState.ConDatos) estado.copy(filtro = evento.estado) else estado
            }
            ListaEvento.SimularErrorCarga -> {
                simulador.simularErrorEnProximaCarga()
                cargar()
            }
            ListaEvento.SimularErrorGuardado -> simulador.simularErrorEnProximoGuardado()
            ListaEvento.VaciarDatos -> simulador.vaciar()
            ListaEvento.RestaurarEjemplos -> simulador.restaurarEjemplos()
        }
    }

    private fun cargar() {
        cargaJob?.cancel()
        _uiState.update { ListaUiState.Cargando } // inmediato, antes de iniciar la operación
        cargaJob = viewModelScope.launch {
            try {
                // Carga inicial asincrónica (simulada con delay en el repositorio).
                repositorio.obtenerTodos()
                // Luego se observan los cambios (p. ej. un recordatorio recién creado).
                repositorio.observarTodos().collect { lista ->
                    _uiState.update { anterior ->
                        if (lista.isEmpty()) {
                            ListaUiState.Vacio
                        } else {
                            ListaUiState.ConDatos(lista, filtro = (anterior as? ListaUiState.ConDatos)?.filtro)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { ListaUiState.Error(e.message ?: "Ocurrió un error inesperado.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as RecordatoriosApplication).container
                ListaViewModel(container.repositorio, container.simulador)
            }
        }
    }
}
