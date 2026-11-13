package cl.ipp.recordatorios.data

import cl.ipp.recordatorios.model.Recordatorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementación en memoria del repositorio. Usa `delay` para simular la latencia
 * de una operación real (sin bloquear el hilo principal) y permite forzar errores.
 */
class InMemoryRecordatorioRepository(
    iniciales: List<Recordatorio> = DatosSimulados.recordatorios,
    private val latenciaCargaMs: Long = 1_200,
    private val latenciaGuardadoMs: Long = 1_000,
) : RecordatorioRepository, SimuladorDatos {

    private val datos = MutableStateFlow(iniciales)
    private val fallarCarga = AtomicBoolean(false)
    private val fallarGuardado = AtomicBoolean(false)

    override suspend fun obtenerTodos(): List<Recordatorio> {
        delay(latenciaCargaMs)
        if (fallarCarga.getAndSet(false)) {
            throw ErrorSimuladoException("No se pudieron cargar los recordatorios. Revisa tu conexión e inténtalo de nuevo.")
        }
        return datos.value
    }

    override fun observarTodos(): Flow<List<Recordatorio>> = datos.asStateFlow()

    override suspend fun obtenerPorId(id: String): Recordatorio? {
        delay(latenciaCargaMs / 2)
        return datos.value.firstOrNull { it.id == id }
    }

    override suspend fun guardar(recordatorio: Recordatorio): Recordatorio {
        delay(latenciaGuardadoMs)
        if (fallarGuardado.getAndSet(false)) {
            throw ErrorSimuladoException("No se pudo guardar el recordatorio. Inténtalo nuevamente.")
        }
        datos.update { lista -> lista.filterNot { it.id == recordatorio.id } + recordatorio }
        return recordatorio
    }

    override fun simularErrorEnProximaCarga() = fallarCarga.set(true)
    override fun simularErrorEnProximoGuardado() = fallarGuardado.set(true)
    override fun vaciar() = datos.update { emptyList() }
    override fun restaurarEjemplos() = datos.update { DatosSimulados.recordatorios }
}
