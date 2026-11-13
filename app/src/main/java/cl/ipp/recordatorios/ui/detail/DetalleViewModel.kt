package cl.ipp.recordatorios.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.ipp.recordatorios.RecordatoriosApplication
import cl.ipp.recordatorios.data.RecordatorioRepository
import cl.ipp.recordatorios.model.Recordatorio
import cl.ipp.recordatorios.ui.navigation.Rutas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Encontrado(val recordatorio: Recordatorio) : DetalleUiState
    data object NoEncontrado : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

/**
 * Recibe únicamente el identificador desde la ruta de navegación (SavedStateHandle)
 * y obtiene el registro completo desde el repositorio.
 */
class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: RecordatorioRepository,
) : ViewModel() {

    private val id: String = checkNotNull(savedStateHandle[Rutas.ARG_ID]) { "Falta el argumento ${Rutas.ARG_ID}" }

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun reintentar() = cargar()

    private fun cargar() {
        _uiState.update { DetalleUiState.Cargando }
        viewModelScope.launch {
            try {
                val r = repositorio.obtenerPorId(id)
                _uiState.update { if (r == null) DetalleUiState.NoEncontrado else DetalleUiState.Encontrado(r) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { DetalleUiState.Error(e.message ?: "No se pudo cargar el recordatorio.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as RecordatoriosApplication).container
                DetalleViewModel(createSavedStateHandle(), container.repositorio)
            }
        }
    }
}
