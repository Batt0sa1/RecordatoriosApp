package cl.ipp.recordatorios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.ipp.recordatorios.ui.create.CrearScreen
import cl.ipp.recordatorios.ui.detail.DetalleScreen
import cl.ipp.recordatorios.ui.list.ListaScreen

/** Rutas del grafo. Al detalle solo viaja el identificador, nunca el objeto completo. */
object Rutas {
    const val LISTA = "lista"
    const val CREAR = "crear"
    const val ARG_ID = "id"
    const val DETALLE = "detalle/{$ARG_ID}"
    fun detalle(id: String) = "detalle/$id"
}

/**
 * Grafo de navegación. El NavController se crea aquí (nivel superior) y las pantallas
 * solo reciben lambdas: ningún componente reutilizable conoce el NavController.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // Evita sacar el listado de la pila si se toca "volver" dos veces seguidas.
    val volver: () -> Unit = {
        if (navController.previousBackStackEntry != null) navController.popBackStack()
    }
    NavHost(navController = navController, startDestination = Rutas.LISTA, modifier = modifier) {
        composable(Rutas.LISTA) {
            ListaScreen(
                onCrearRecordatorio = { navController.navigate(Rutas.CREAR) { launchSingleTop = true } },
                onRecordatorioClick = { id -> navController.navigate(Rutas.detalle(id)) },
            )
        }
        composable(Rutas.CREAR) {
            CrearScreen(
                // popBackStack() vuelve al listado existente sin apilar una nueva copia.
                onGuardar = volver,
                onCancelar = volver,
            )
        }
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.StringType }),
        ) {
            DetalleScreen(onVolver = volver)
        }
    }
}
