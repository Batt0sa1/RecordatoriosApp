package cl.ipp.recordatorios

import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.model.Categoria
import cl.ipp.recordatorios.model.ErrorValidacion
import cl.ipp.recordatorios.model.EstadoRecordatorio
import cl.ipp.recordatorios.model.Prioridad
import cl.ipp.recordatorios.model.ResultadoValidacion
import cl.ipp.recordatorios.model.Validador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModeloTest {

    @Test
    fun tituloConEspaciosYMenosDeCuatroCaracteresEsInvalido() {
        val r = Validador.validar("  abc  ", null, Categoria.Trabajo, Prioridad.Alta)
        assertEquals(ResultadoValidacion.Invalido(listOf(ErrorValidacion.TituloMuyCorto)), r)
    }

    @Test
    fun descripcionMayorA200EsInvalida() {
        val r = Validador.validar("Título válido", "x".repeat(201), Categoria.Trabajo, Prioridad.Alta)
        assertEquals(ResultadoValidacion.Invalido(listOf(ErrorValidacion.DescripcionMuyLarga)), r)
    }

    @Test
    fun faltanCategoriaYPrioridad() {
        val r = Validador.validar("Título válido", null, null, null) as ResultadoValidacion.Invalido
        assertEquals(listOf(ErrorValidacion.CategoriaInvalida, ErrorValidacion.PrioridadNoSeleccionada), r.errores)
    }

    @Test
    fun crearRecordatorioRecortaElTitulo() {
        val r = Validador.crearRecordatorio("x", "  Comprar pan  ", "  ", Categoria.Hogar, Prioridad.Baja).getOrThrow()
        assertEquals("Comprar pan", r.titulo)
        assertEquals(null, r.descripcion)
    }

    @Test
    fun datosSimuladosDescartanElInvalido() {
        assertEquals(7, DatosSimulados.recordatorios.size)
        assertTrue(DatosSimulados.recordatorios.none { it.id == "8" })
    }

    @Test
    fun colecciones() {
        assertEquals(5, DatosSimulados.categoriasUsadas().size)
        assertEquals(2, DatosSimulados.filtrarPorEstado(EstadoRecordatorio.Completado).size)
        assertEquals(2, DatosSimulados.filtrarPorPrioridad(Prioridad.Alta).size)
        assertEquals(DatosSimulados.recordatorios.size, DatosSimulados.conteoPorEstado().values.sum())
        assertTrue(Categoria.todas.all { it in DatosSimulados.etiquetasPorCategoria })
    }
}
