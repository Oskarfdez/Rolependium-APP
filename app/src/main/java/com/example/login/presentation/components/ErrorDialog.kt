package com.example.login.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Composable que muestra un diálogo de error con un mensaje y un botón para cerrarlo
@Composable
fun EventDialog(
    modifier: Modifier = Modifier,  // Modificador opcional para personalizar el diseño del diálogo
    @StringRes errorMessage: Int,  // ID del recurso de cadena con el mensaje de error que se mostrará
    onDismiss: (() -> Unit)? = null  // Función que se ejecuta cuando el diálogo es cerrado (opcional)
) {
    // Creamos un AlertDialog que se utiliza para mostrar el mensaje de error
    AlertDialog(
        modifier = modifier  // Aplica el modificador proporcionado, como el padding o fondo
            .background(androidx.compose.ui.graphics.Color.White)  // Fondo blanco para el diálogo
            .padding(16.dp),  // Padding alrededor del contenido del diálogo
        onDismissRequest = { onDismiss?.invoke() },  // Llama a la función de cierre cuando se solicita el cierre del diálogo
        title = {
            // Título del diálogo que dice "Error"
            Text(
                "Error",  // Título estático del diálogo
                style = TextStyle(
                    color = colorScheme.onSurface,  // Color del texto basado en el tema
                    fontSize = 20.sp,  // Tamaño de la fuente
                    fontWeight = FontWeight.Bold  // Hace que el título sea en negrita
                )
            )
        },
        text = {
            // Cuerpo del diálogo con el mensaje de error, que es un recurso de texto
            Text(
                text = LocalContext.current.getString(errorMessage),  // Obtiene el mensaje de error usando el recurso
                style = TextStyle(
                    color = colorScheme.onSurface,  // Color del texto basado en el tema
                    fontSize = 16.sp  // Tamaño de la fuente
                )
            )
        },
        buttons = {
            // Coloca el botón "Aceptar" para cerrar el diálogo
            Row(
                modifier = Modifier
                    .fillMaxWidth()  // Hace que la fila ocupe el ancho disponible
                    .padding(8.dp),  // Padding alrededor de la fila de botones
                horizontalArrangement = Arrangement.End  // Alinea el botón a la derecha
            ) {
                // Botón "Aceptar" que cierra el diálogo cuando es presionado
                TextButton(onClick = { onDismiss?.invoke() }) {
                    Text(text = "Aceptar", style = MaterialTheme.typography.h5)  // El texto del botón
                }
            }

})
}