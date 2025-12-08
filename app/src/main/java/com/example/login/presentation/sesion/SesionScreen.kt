package com.example.login.presentation.sesion


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionScreen(
    email: String,
    onNavigateToList: () -> Unit,
    onNavigateToDice: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSpells: () -> Unit,
    onNavigateToCreator: () -> Unit,
    onNavigateToData: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SesionViewModel = viewModel()
    val context = LocalContext.current

    // Estados observados del ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val sesiones by viewModel.sesiones.collectAsState()
    val error by viewModel.error.collectAsState()

    // Inicializar el email y cargar sesiones
    LaunchedEffect(email) {
        viewModel.setEmail(email)
        viewModel.loadSesiones(context)
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateToUser = onNavigateToUser,
                onBack = onBack
            )
        },
        bottomBar = {
            BottomBar(
                onNavigateToDice = onNavigateToDice,
                onNavigateToList = onNavigateToList,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSpells = onNavigateToSpells
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para recargar
                FloatingActionButton(
                    onClick = {
                        viewModel.loadSesiones(context)
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Refresh, "Recargar sesiones")
                }

                // Botón para crear nueva sesión
                FloatingActionButton(
                    onClick = {
                        onNavigateToCreator()
                    }
                ) {
                    Icon(Icons.Default.Add, "Crear nueva sesión")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Título de la pantalla
            Text(
                text = "Mis Sesiones: $email",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            // Contador de sesiones
            if (!isLoading && sesiones.isNotEmpty()) {
                Text(
                    text = "${sesiones.size} sesión${if (sesiones.size != 1) "es" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (isLoading) {
                // Estado de carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Cargando sesiones...")
                    }
                }
            } else if (error.isNotEmpty()) {
                // Estado de error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadSesiones(context) }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            } else if (sesiones.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = "No sessions",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No estás en ninguna sesión",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Únete a una sesión existente o crea una nueva",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista de sesiones
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sesiones) { sesion ->
                        SesionCard(
                            sesion = sesion,
                            currentUserEmail = email,
                            onNavigateToData = {
                                onNavigateToData(sesion.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SesionCard(
    sesion: SesionState,
    currentUserEmail: String,
    onNavigateToData: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = { onNavigateToData() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado con nombre y badge si es master
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sesion.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (sesion.masterEmail == currentUserEmail) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "MASTER",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información de la sesión
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Master
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Master:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (sesion.masterEmail == currentUserEmail) {
                            "Tú"
                        } else {
                            sesion.masterNombre ?: "Cargando..."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (sesion.masterEmail == currentUserEmail) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                // Jugadores
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Jugadores:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${sesion.jugadores.size} jugador${if (sesion.jugadores.size != 1) "es" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Fecha de creación
                sesion.fechaCreacion?.let { fecha ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Creada:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDate(fecha),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Descripción
            sesion.descripcion?.let { descripcion ->
                if (descripcion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Función para formatear la fecha
private fun formatDate(timestamp: com.google.firebase.Timestamp): String {
    val date = timestamp.toDate()
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(date)
}

// Data class para el estado de la sesión

@Preview(showBackground = true)
@Composable
fun SesionScreenPreview() {
    MaterialTheme {
        SesionScreen(
            email = "usuario@ejemplo.com",
            onNavigateToList = {},
            onNavigateToDice = {},
            onNavigateToUser = {},
            onNavigateToHome = {},
            onNavigateToSpells = {},
            onNavigateToCreator = {},
            onNavigateToData = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
fun SesionCardPreview() {
    val sesionEjemplo = SesionState(
        id = "1",
        nombre = "La Búsqueda del Dragón",
        descripcion = "Una épica aventura para derrotar al dragón ancestral que amenaza el reino.",
        masterEmail = "master@ejemplo.com",
        masterNombre = "Gandalf",
        jugadores = listOf("player1@ejemplo.com", "player2@ejemplo.com"),
        horarios = emptyList(),
        notificaciones = emptyList(),
        fechaCreacion = com.google.firebase.Timestamp.now()
    )

    MaterialTheme {
        SesionCard(
            sesion = sesionEjemplo,
            currentUserEmail = "player1@ejemplo.com",
            onNavigateToData = {}
        )
    }
}

@Preview
@Composable
fun SesionCardMasterPreview() {
    val sesionEjemplo = SesionState(
        id = "2",
        nombre = "Mi Aventura",
        descripcion = "Sesión donde soy el master",
        masterEmail = "yo@ejemplo.com",
        masterNombre = "Yo Mismo",
        jugadores = listOf("otro@ejemplo.com"),
        horarios = emptyList(),
        notificaciones = emptyList(),
        fechaCreacion = com.google.firebase.Timestamp.now()
    )

    MaterialTheme {
        SesionCard(
            sesion = sesionEjemplo,
            currentUserEmail = "yo@ejemplo.com",
            onNavigateToData = {}
        )
    }
}