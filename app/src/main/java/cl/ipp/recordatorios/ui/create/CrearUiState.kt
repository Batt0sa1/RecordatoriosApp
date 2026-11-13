package cl.ipp.recordatorios.ui.create

import cl.ipp.recordatorios.model.Categoria
import cl.ipp.recordatorios.model.Prioridad

/** Estado del envío del formulario (mutuamente excluyente). */
sealed interface EstadoEnvio {
    data object Inactivo : EstadoEnvio
    data object Enviando : EstadoEnvio
    data class Exito(val idCreado: String) : EstadoEnvio
    data class Error(val mensaje: String) : EstadoEnvio
}

/** Estado inmutable de la pantalla de creación. */
data class CrearUiState(
    // Valores actuales de los campos
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    // Mensajes de validación por campo (null = sin error visible)
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorPrioridad: String? = null,
    // Validez global calculada por el ViewModel
    val esValido: Boolean = false,
    // Estado y resultado del envío / error general de guardado
    val envio: EstadoEnvio = EstadoEnvio.Inactivo,
) {
    val enviando: Boolean get() = envio is EstadoEnvio.Enviando
    val puedeGuardar: Boolean get() = esValido && !enviando && envio !is EstadoEnvio.Exito
}

/** Eventos que la pantalla de creación envía al ViewModel. */
sealed interface CrearEvento {
    data class TituloCambiado(val valor: String) : CrearEvento
    data class DescripcionCambiada(val valor: String) : CrearEvento
    data class CategoriaSeleccionada(val categoria: Categoria) : CrearEvento
    data class PrioridadSeleccionada(val prioridad: Prioridad) : CrearEvento
    data object Guardar : CrearEvento
    data object DescartarError : CrearEvento
}
