package cl.ipp.recordatorios.model

/**
 * Categorías posibles de un recordatorio.
 * Se modela como sealed class: el compilador conoce todas las variantes,
 * por lo que los `when` sobre una Categoria son exhaustivos.
 */
sealed class Categoria(val id: String, val nombre: String) {
    data object Trabajo : Categoria("trabajo", "Trabajo")
    data object Personal : Categoria("personal", "Personal")
    data object Salud : Categoria("salud", "Salud")
    data object Estudio : Categoria("estudio", "Estudio")
    data object Hogar : Categoria("hogar", "Hogar")

    companion object {
        /** Opciones definidas por la organización (orden de presentación). */
        val todas: List<Categoria> by lazy { listOf(Trabajo, Personal, Salud, Estudio, Hogar) }

        fun desdeId(id: String): Categoria? = todas.firstOrNull { it.id == id }
    }
}
