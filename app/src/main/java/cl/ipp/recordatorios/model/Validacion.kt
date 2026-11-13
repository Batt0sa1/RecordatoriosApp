package cl.ipp.recordatorios.model

const val TITULO_MIN_CARACTERES = 4
const val DESCRIPCION_MAX_CARACTERES = 200

/** Errores de validación posibles, uno por regla de negocio. */
sealed class ErrorValidacion(val mensaje: String) {
    data object TituloMuyCorto :
        ErrorValidacion("El título debe tener al menos $TITULO_MIN_CARACTERES caracteres.")
    data object DescripcionMuyLarga :
        ErrorValidacion("La descripción no puede superar los $DESCRIPCION_MAX_CARACTERES caracteres.")
    data object CategoriaInvalida :
        ErrorValidacion("Selecciona una categoría válida.")
    data object PrioridadNoSeleccionada :
        ErrorValidacion("Selecciona una prioridad.")
}

/** Resultado de validar: o es válido, o contiene la lista de errores. */
sealed interface ResultadoValidacion {
    data object Valido : ResultadoValidacion
    data class Invalido(val errores: List<ErrorValidacion>) : ResultadoValidacion
}

object Validador {

    fun validarTitulo(titulo: String): ErrorValidacion? =
        if (titulo.trim().length < TITULO_MIN_CARACTERES) ErrorValidacion.TituloMuyCorto else null

    fun validarDescripcion(descripcion: String?): ErrorValidacion? =
        if ((descripcion?.length ?: 0) > DESCRIPCION_MAX_CARACTERES) ErrorValidacion.DescripcionMuyLarga else null

    fun validarCategoria(categoria: Categoria?): ErrorValidacion? =
        if (categoria == null || categoria !in Categoria.todas) ErrorValidacion.CategoriaInvalida else null

    fun validarPrioridad(prioridad: Prioridad?): ErrorValidacion? =
        if (prioridad == null) ErrorValidacion.PrioridadNoSeleccionada else null

    /** Valida un conjunto de datos de entrada antes de construir un Recordatorio. */
    fun validar(
        titulo: String,
        descripcion: String?,
        categoria: Categoria?,
        prioridad: Prioridad?,
    ): ResultadoValidacion {
        val errores = listOfNotNull(
            validarTitulo(titulo),
            validarDescripcion(descripcion),
            validarCategoria(categoria),
            validarPrioridad(prioridad),
        )
        return if (errores.isEmpty()) ResultadoValidacion.Valido else ResultadoValidacion.Invalido(errores)
    }

    /**
     * Crea un recordatorio solo si los datos son válidos, evitando estados inválidos.
     * El título se guarda sin espacios iniciales/finales y una descripción vacía se trata como ausente.
     */
    fun crearRecordatorio(
        id: String,
        titulo: String,
        descripcion: String?,
        categoria: Categoria?,
        prioridad: Prioridad?,
        estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
        fechaLimite: java.time.LocalDate? = null,
        ubicacion: String? = null,
    ): Result<Recordatorio> =
        when (val r = validar(titulo, descripcion, categoria, prioridad)) {
            is ResultadoValidacion.Invalido ->
                Result.failure(IllegalArgumentException(r.errores.joinToString(" ") { it.mensaje }))
            ResultadoValidacion.Valido -> Result.success(
                Recordatorio(
                    id = id,
                    titulo = titulo.trim(),
                    descripcion = descripcion?.trim()?.takeIf { it.isNotEmpty() },
                    categoria = categoria!!,
                    prioridad = prioridad!!,
                    estado = estado,
                    fechaLimite = fechaLimite,
                    ubicacion = ubicacion?.trim()?.takeIf { it.isNotEmpty() },
                )
            )
        }
}
