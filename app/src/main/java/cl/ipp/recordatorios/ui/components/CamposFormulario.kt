package cl.ipp.recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cl.ipp.recordatorios.model.Categoria
import cl.ipp.recordatorios.model.Prioridad

/** Texto de error con ícono: el color nunca es el único indicador de un error. */
@Composable
fun MensajeError(mensaje: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Filled.Warning, contentDescription = null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
        Text(mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Campo de texto controlado: no guarda estado propio; recibe el valor y
 * notifica los cambios mediante `onValorChange`.
 */
@Composable
fun CampoTextoValidado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    maxCaracteres: Int? = null,
    singleLine: Boolean = true,
    habilitado: Boolean = true,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        isError = error != null,
        enabled = habilitado,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        supportingText = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (error != null) MensajeError(error, Modifier.weight(1f)) else Text("")
                if (maxCaracteres != null) Text("${valor.length}/$maxCaracteres")
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (error != null) error(error) },
    )
}

/** Selector controlado de categoría: solo ofrece las opciones definidas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCategoria(
    seleccionada: Categoria?,
    onSeleccion: (Categoria) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
    opciones: List<Categoria> = Categoria.todas,
    habilitado: Boolean = true,
) {
    // Estado puramente visual (menú abierto/cerrado): puede vivir en el composable.
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { if (habilitado) expandido = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = seleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = habilitado,
            label = { Text("Categoría") },
            placeholder = { Text("Selecciona una categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            isError = error != null,
            supportingText = { if (error != null) MensajeError(error) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .semantics { if (error != null) error(error) },
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.nombre) },
                    onClick = {
                        onSeleccion(categoria)
                        expandido = false
                    },
                )
            }
        }
    }
}

/** Selector controlado de prioridad mediante FilterChips. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorPrioridad(
    seleccionada: Prioridad?,
    onSeleccion: (Prioridad) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    Column(
        modifier.semantics { if (error != null) error(error) },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("Prioridad", style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Prioridad.todas.forEach { prioridad ->
                val elegida = prioridad == seleccionada
                FilterChip(
                    selected = elegida,
                    onClick = { onSeleccion(prioridad) },
                    enabled = habilitado,
                    label = { Text(prioridad.nombre) },
                    leadingIcon = if (elegida) {
                        { Icon(Icons.Filled.Check, contentDescription = null, Modifier.size(18.dp)) }
                    } else null,
                )
            }
        }
        if (error != null) MensajeError(error)
    }
}
