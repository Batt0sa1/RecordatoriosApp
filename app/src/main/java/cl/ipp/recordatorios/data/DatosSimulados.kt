package cl.ipp.recordatorios.data

import cl.ipp.recordatorios.model.Categoria
import cl.ipp.recordatorios.model.EstadoRecordatorio
import cl.ipp.recordatorios.model.Prioridad
import cl.ipp.recordatorios.model.Recordatorio
import cl.ipp.recordatorios.model.Validador
import java.time.LocalDate

/** Etiqueta visual asociada a una categoría. El color se guarda como ARGB para no depender de Compose. */
data class EtiquetaCategoria(val texto: String, val colorArgb: Long)

object DatosSimulados {

    /** Map: asocia cada categoría con su etiqueta y color. */
    val etiquetasPorCategoria: Map<Categoria, EtiquetaCategoria> = mapOf(
        Categoria.Trabajo to EtiquetaCategoria("💼 Trabajo", 0xFF1565C0),
        Categoria.Personal to EtiquetaCategoria("🙂 Personal", 0xFF6A1B9A),
        Categoria.Salud to EtiquetaCategoria("❤️ Salud", 0xFFC62828),
        Categoria.Estudio to EtiquetaCategoria("📚 Estudio", 0xFF2E7D32),
        Categoria.Hogar to EtiquetaCategoria("🏠 Hogar", 0xFFEF6C00),
    )

    fun etiquetaDe(categoria: Categoria): EtiquetaCategoria =
        etiquetasPorCategoria[categoria] ?: EtiquetaCategoria(categoria.nombre, 0xFF607D8B)

    private val hoy: LocalDate = LocalDate.now()

    /**
     * List: recordatorios simulados. Cada uno pasa por el Validador, por lo que
     * un dato simulado inválido no llega nunca a la interfaz.
     */
    val recordatorios: List<Recordatorio> = listOf(
        Validador.crearRecordatorio("1", "Entregar informe mensual", "Enviar el informe de gestión al equipo regional.",
            Categoria.Trabajo, Prioridad.Alta, EstadoRecordatorio.EnProgreso, hoy.plusDays(2)),
        Validador.crearRecordatorio("2", "Control médico anual", "Llevar exámenes de sangre.",
            Categoria.Salud, Prioridad.Alta, EstadoRecordatorio.Pendiente, hoy.plusDays(7), "CESFAM Centro"),
        Validador.crearRecordatorio("3", "Estudiar Kotlin: colecciones", null,
            Categoria.Estudio, Prioridad.Media, EstadoRecordatorio.Completado),
        Validador.crearRecordatorio("4", "Pagar cuenta de la luz", "Vence a fin de mes.",
            Categoria.Hogar, Prioridad.Media, EstadoRecordatorio.Vencido, hoy.minusDays(1)),
        Validador.crearRecordatorio("5", "Llamar a la abuela", null,
            Categoria.Personal, Prioridad.Baja, EstadoRecordatorio.Pendiente),
        Validador.crearRecordatorio("6", "Reunión de planificación", "Revisar avances del módulo 1.",
            Categoria.Trabajo, Prioridad.Media, EstadoRecordatorio.Pendiente, hoy.plusDays(1), "Sala 3"),
        Validador.crearRecordatorio("7", "Comprar materiales", "Cuadernos y destacadores.",
            Categoria.Estudio, Prioridad.Baja, EstadoRecordatorio.Completado),
        // Dato inválido a propósito (título de 2 caracteres): el Validador lo rechaza y se descarta.
        Validador.crearRecordatorio("8", "  ok ", null, Categoria.Hogar, Prioridad.Baja),
    ).mapNotNull { it.getOrNull() }

    /** Set: categorías realmente usadas, sin repetidos. */
    fun categoriasUsadas(lista: List<Recordatorio> = recordatorios): Set<Categoria> =
        lista.map { it.categoria }.toSet()

    fun filtrarPorEstado(estado: EstadoRecordatorio, lista: List<Recordatorio> = recordatorios): List<Recordatorio> =
        lista.filter { it.estado == estado }

    fun filtrarPorPrioridad(prioridad: Prioridad, lista: List<Recordatorio> = recordatorios): List<Recordatorio> =
        lista.filter { it.prioridad == prioridad }

    /** Map: cantidad de recordatorios por estado (incluye estados con 0). */
    fun conteoPorEstado(lista: List<Recordatorio> = recordatorios): Map<EstadoRecordatorio, Int> =
        EstadoRecordatorio.todos.associateWith { estado -> lista.count { it.estado == estado } }

    fun agruparPorCategoria(lista: List<Recordatorio> = recordatorios): Map<Categoria, List<Recordatorio>> =
        lista.groupBy { it.categoria }

    /** Orden: primero mayor prioridad, luego por título. */
    fun ordenarPorPrioridad(lista: List<Recordatorio> = recordatorios): List<Recordatorio> =
        lista.sortedWith(compareByDescending<Recordatorio> { it.prioridad.peso }.thenBy { it.titulo })
}
