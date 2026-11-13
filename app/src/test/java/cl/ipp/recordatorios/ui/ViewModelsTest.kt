package cl.ipp.recordatorios.ui

import androidx.lifecycle.SavedStateHandle
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.data.InMemoryRecordatorioRepository
import cl.ipp.recordatorios.model.Categoria
import cl.ipp.recordatorios.model.Prioridad
import cl.ipp.recordatorios.ui.create.CrearEvento
import cl.ipp.recordatorios.ui.create.CrearViewModel
import cl.ipp.recordatorios.ui.create.EstadoEnvio
import cl.ipp.recordatorios.ui.detail.DetalleUiState
import cl.ipp.recordatorios.ui.detail.DetalleViewModel
import cl.ipp.recordatorios.ui.list.ListaEvento
import cl.ipp.recordatorios.ui.list.ListaUiState
import cl.ipp.recordatorios.ui.list.ListaViewModel
import cl.ipp.recordatorios.ui.navigation.Rutas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: InMemoryRecordatorioRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = InMemoryRecordatorioRepository()
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun listaPasaDeCargandoAConDatos() = runTest(dispatcher) {
        val vm = ListaViewModel(repo, repo)
        assertEquals(ListaUiState.Cargando, vm.uiState.value)
        advanceUntilIdle()
        val estado = vm.uiState.value as ListaUiState.ConDatos
        assertEquals(DatosSimulados.recordatorios.size, estado.recordatorios.size)
    }

    @Test
    fun listaVaciaYErrorConReintento() = runTest(dispatcher) {
        val vm = ListaViewModel(repo, repo)
        advanceUntilIdle()
        vm.onEvento(ListaEvento.VaciarDatos)
        advanceUntilIdle()
        assertEquals(ListaUiState.Vacio, vm.uiState.value)

        vm.onEvento(ListaEvento.RestaurarEjemplos)
        vm.onEvento(ListaEvento.SimularErrorCarga)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is ListaUiState.Error)

        vm.onEvento(ListaEvento.Reintentar)
        assertEquals(ListaUiState.Cargando, vm.uiState.value)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is ListaUiState.ConDatos)
    }

    @Test
    fun filtroPorEstadoSeConservaEnElUiState() = runTest(dispatcher) {
        val vm = ListaViewModel(repo, repo)
        advanceUntilIdle()
        vm.onEvento(ListaEvento.FiltrarPorEstado(cl.ipp.recordatorios.model.EstadoRecordatorio.Completado))
        val estado = vm.uiState.value as ListaUiState.ConDatos
        assertEquals(2, estado.visibles.size)
    }

    private fun CrearViewModel.completarValido() {
        onEvento(CrearEvento.TituloCambiado("  Comprar pan  "))
        onEvento(CrearEvento.CategoriaSeleccionada(Categoria.Hogar))
        onEvento(CrearEvento.PrioridadSeleccionada(Prioridad.Media))
    }

    @Test
    fun formularioInvalidoNoPermiteGuardar() {
        val vm = CrearViewModel(repo)
        assertFalse(vm.uiState.value.puedeGuardar)
        vm.onEvento(CrearEvento.TituloCambiado("  ab  "))
        assertEquals("El título debe tener al menos 4 caracteres.", vm.uiState.value.errorTitulo)
        assertFalse(vm.uiState.value.puedeGuardar)
        vm.onEvento(CrearEvento.DescripcionCambiada("x".repeat(201)))
        assertTrue(vm.uiState.value.errorDescripcion != null)
    }

    @Test
    fun guardadoExitosoDeshabilitaElBotonMientrasEnvia() = runTest(dispatcher) {
        val vm = CrearViewModel(repo) { "nuevo" }
        vm.completarValido()
        assertTrue(vm.uiState.value.puedeGuardar)

        vm.onEvento(CrearEvento.Guardar)
        assertTrue(vm.uiState.value.enviando)
        assertFalse(vm.uiState.value.puedeGuardar)

        advanceUntilIdle()
        assertEquals(EstadoEnvio.Exito("nuevo"), vm.uiState.value.envio)
        assertEquals("Comprar pan", repo.obtenerPorId("nuevo")?.titulo)
    }

    @Test
    fun guardadoConErrorSimuladoPermiteReintentar() = runTest(dispatcher) {
        val vm = CrearViewModel(repo)
        vm.completarValido()
        repo.simularErrorEnProximoGuardado()
        vm.onEvento(CrearEvento.Guardar)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.envio is EstadoEnvio.Error)
        assertTrue(vm.uiState.value.puedeGuardar)

        vm.onEvento(CrearEvento.Guardar)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.envio is EstadoEnvio.Exito)
    }

    @Test
    fun detalleRecibeSoloElId() = runTest(dispatcher) {
        val vm = DetalleViewModel(SavedStateHandle(mapOf(Rutas.ARG_ID to "2")), repo)
        advanceUntilIdle()
        val estado = vm.uiState.value as DetalleUiState.Encontrado
        assertEquals("Control médico anual", estado.recordatorio.titulo)

        val vm2 = DetalleViewModel(SavedStateHandle(mapOf(Rutas.ARG_ID to "no-existe")), repo)
        advanceUntilIdle()
        assertEquals(DetalleUiState.NoEncontrado, vm2.uiState.value)
    }
}
