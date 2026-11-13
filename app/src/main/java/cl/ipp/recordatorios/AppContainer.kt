package cl.ipp.recordatorios

import android.app.Application
import cl.ipp.recordatorios.data.InMemoryRecordatorioRepository
import cl.ipp.recordatorios.data.RecordatorioRepository
import cl.ipp.recordatorios.data.SimuladorDatos

/** Contenedor de dependencias manual: una única instancia del repositorio para toda la app. */
class AppContainer {
    private val enMemoria = InMemoryRecordatorioRepository()
    val repositorio: RecordatorioRepository = enMemoria
    val simulador: SimuladorDatos = enMemoria
}

class RecordatoriosApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
