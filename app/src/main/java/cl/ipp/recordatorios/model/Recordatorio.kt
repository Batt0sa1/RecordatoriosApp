package cl.ipp.recordatorios.model

import java.time.LocalDate

/**
 * Recordatorio inmutable: todas las propiedades son `val`.
 * Para "modificar" un recordatorio se crea una copia con `copy(...)`.
 *
 * Los datos opcionales (descripción, fecha límite, ubicación) se representan
 * como tipos anulables, porque no todos los recordatorios los requieren.
 */
data class Recordatorio(
    val id: String,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    val fechaLimite: LocalDate? = null,
    val ubicacion: String? = null,
)
