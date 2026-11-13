package cl.ipp.recordatorios

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.ipp.recordatorios.data.DatosSimulados
import cl.ipp.recordatorios.ui.PantallaInicial
import cl.ipp.recordatorios.ui.theme.RecordatoriosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("Recordatorios", "Recordatorios simulados válidos: ${DatosSimulados.recordatorios.size}")
        setContent {
            RecordatoriosTheme {
                PantallaInicial(recordatorios = DatosSimulados.recordatorios)
            }
        }
    }
}
