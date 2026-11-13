package cl.ipp.recordatorios.data

import cl.ipp.recordatorios.model.Recordatorio
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de la fuente de datos. Los ViewModel dependen de esta interfaz y no de
 * una lista concreta, por lo que más adelante podrá reemplazarse por Room o un
 * servicio remoto sin modificar pantallas ni componentes.
 */
interface RecordatorioRepository {
    /** Carga (simulada como asincrónica) de todos los recordatorios. Puede lanzar una excepción. */
    suspend fun obtenerTodos(): List<Recordatorio>

    /** Flujo con la lista actual; emite de nuevo cada vez que cambia. */
    fun observarTodos(): Flow<List<Recordatorio>>

    suspend fun obtenerPorId(id: String): Recordatorio?

    /** Guarda (crea o reemplaza) un recordatorio y lo devuelve. Puede lanzar una excepción. */
    suspend fun guardar(recordatorio: Recordatorio): Recordatorio
}

/**
 * Mecanismo de simulación para comprobar todos los estados de la interfaz
 * (vacío, error de carga, error de guardado) sin base de datos ni servicio remoto.
 */
interface SimuladorDatos {
    fun simularErrorEnProximaCarga()
    fun simularErrorEnProximoGuardado()
    fun vaciar()
    fun restaurarEjemplos()
}

class ErrorSimuladoException(mensaje: String) : Exception(mensaje)
