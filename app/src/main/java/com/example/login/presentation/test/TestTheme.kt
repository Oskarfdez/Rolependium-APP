package com.example.login.presentation.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.login.ui.theme.LoginTheme

@Composable
fun TestThemeScreen() {//ESTA FUNCION YA NO SE USA, LA CREE PARA PROBAR A TIEMPO REAL SI LOS TEMAS SE IMPLEMENTABAN CORRECTAMENTE
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Prueba del Tema",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Primary Container",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Button(onClick = {}) {
            Text("Botón de Prueba")
        }

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Campo de prueba") }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Colores actuales:")
            Text("Primary: ${MaterialTheme.colorScheme.primary}")
            Text("Surface: ${MaterialTheme.colorScheme.surface}")
            Text("Background: ${MaterialTheme.colorScheme.background}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestThemeScreenPreview() {
    LoginTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TestThemeScreen()
        }
    }
}