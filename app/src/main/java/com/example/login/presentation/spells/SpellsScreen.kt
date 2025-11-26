package com.example.login.presentation.spells

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellsScreen(
    onNavigateToList: () -> Unit,
    onNavigateToDice: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    email: String
) {
    val viewModel: SpellsViewModel = viewModel()
    val context = LocalContext.current
    val spells by viewModel.spells.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Cargar cartas al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadSpells(context)
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
            if (!isLoading) {
                FloatingActionButton(
                    onClick = { viewModel.loadSpells(context) }
                ) {
                    Icon(Icons.Default.Refresh, "Recargar cartas")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Título
            Text(
                text = "Colección de Cartas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Cargando cartas...")
                    }
                }
            } else if (spells.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron cartas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Lista de cartas
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(spells) { spell ->
                        CartaItem(spell = spell)
                    }
                }
            }
        }
    }
}

@Composable
fun CartaItem(spell: SpellsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Nombre de la carta
            Text(
                text = spell.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Información en filas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Escuela
                Column {
                    Text(
                        text = "Escuela",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = spell.escuela,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Nivel
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Nivel",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = spell.nivel.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clases
            Column {
                Text(
                    text = "Clases",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = spell.clases,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Versión alternativa con diseño horizontal
@Composable
fun CartaItemHorizontal(spell: SpellsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna izquierda - Nombre y escuela
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = spell.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = spell.escuela,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Columna derecha - Nivel y clases
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "Nvl ${spell.nivel}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = spell.clases,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun CartasScreenPreview() {
    MaterialTheme {
        SpellsScreen(
            onNavigateToList = {},
            onNavigateToDice = {},
            onNavigateToUser = {},
            onNavigateToHome = {},
            onBack = {},
            email = ""
        )
    }
}

@Preview
@Composable
fun CartaItemPreview() {
    val cartaEjemplo = SpellsState(
        nombre = "Bola de Fuego",
        escuela = "Evocación",
        nivel = 3,
        clases = "Mago, Hechicero",
        descripcion = "Lanza una esfera de fuego que explota al impactar"
    )

    MaterialTheme {
        CartaItem(spell = cartaEjemplo)
    }
}