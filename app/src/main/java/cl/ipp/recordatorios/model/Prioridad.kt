package cl.ipp.recordatorios.model

/** Prioridad del recordatorio. `peso` permite ordenar de mayor a menor urgencia. */
sealed class Prioridad(val nombre: String, val peso: Int) {
    data object Alta : Prioridad("Alta", 3)
    data object Media : Prioridad("Media", 2)
    data object Baja : Prioridad("Baja", 1)

    companion object {
        val todas: List<Prioridad> by lazy { listOf(Alta, Media, Baja) }
    }
}
