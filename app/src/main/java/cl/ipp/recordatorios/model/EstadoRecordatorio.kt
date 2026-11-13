package cl.ipp.recordatorios.model

/** Estado del ciclo de vida de un recordatorio. */
sealed class EstadoRecordatorio(val nombre: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object EnProgreso : EstadoRecordatorio("En progreso")
    data object Completado : EstadoRecordatorio("Completado")
    data object Vencido : EstadoRecordatorio("Vencido")

    companion object {
        val todos: List<EstadoRecordatorio> by lazy { listOf(Pendiente, EnProgreso, Completado, Vencido) }
    }
}
