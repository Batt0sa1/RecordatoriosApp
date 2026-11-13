package cl.ipp.recordatorios.ui.list

import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.EstadoRecordatorio
import cl.ipp.recordatorios.model.Recordatorio

/**
 * Estados mutuamente excluyentes del listado. Al ser sealed interface no es posible
 * representar a la vez "cargando" y "error" o "contenido" y "vacío".
 */
sealed interface ListaUiState {
    data object Cargando : ListaUiState
    data object Vacio : ListaUiState
    data class ConDatos(
        val recordatorios: List<Recordatorio>,
        val filtro: EstadoRecordatorio? = null,
    ) : ListaUiState {
        val visibles: List<Recordatorio> =
            DatosSimulados.ordenarPorPrioridad(filtro?.let { DatosSimulados.filtrarPorEstado(it, recordatorios) } ?: recordatorios)
        val conteoPorEstado: Map<EstadoRecordatorio, Int> = DatosSimulados.conteoPorEstado(recordatorios)
    }
    data class Error(val mensaje: String) : ListaUiState
}

/** Eventos que la pantalla de listado envía al ViewModel. */
sealed interface ListaEvento {
    data object Reintentar : ListaEvento
    data class FiltrarPorEstado(val estado: EstadoRecordatorio?) : ListaEvento
    data object SimularErrorCarga : ListaEvento
    data object SimularErrorGuardado : ListaEvento
    data object VaciarDatos : ListaEvento
    data object RestaurarEjemplos : ListaEvento
}
