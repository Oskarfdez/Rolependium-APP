package com.example.login.presentation.charlist

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import com.example.login.ui.theme.ROLERED

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonajeListScreen(
    email: String,
    onNavigateToCreator: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToData: (String) -> Unit,
    onNavigateToDice: () -> Unit,
    onNavigateToSpells: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: CharListViewModel = hiltViewModel()
    val personajes by viewModel.characters.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarGuerreros(email)
    }

    Scaffold(
        topBar = { TopBar(onNavigateToUser,onBack)  },
        bottomBar = {
            BottomBar(
                modifier = Modifier,
                onNavigateToDice,
                onNavigateToList,
                onNavigateToHome
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreator,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear personaje"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(55.dp))
            Text(
                text = "Listado de Personajes",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(personajes) { personaje ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onNavigateToData(personaje.id) },
                        colors = CardDefaults.cardColors(containerColor = ROLERED)
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${personaje.nombre} - ${personaje.clase}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}