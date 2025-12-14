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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import com.example.login.presentation.sesioncreator.SesionState

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

    // Estados para unirse
    var sesionIdInput by remember { mutableStateOf("") }
    val showJoinDialog by viewModel.showJoinDialog.collectAsState()
    val joinDialogMessage by viewModel.joinDialogMessage.collectAsState()

    // States
    val isLoading by viewModel.isLoading.collectAsState()
    val sesiones by viewModel.sesiones.collectAsState()
    val error by viewModel.error.collectAsState()

    // Cargar sesiones al iniciar
    LaunchedEffect(email) {
        viewModel.setEmail(email)
        viewModel.loadSesiones(context)
    }

    // AlertDialog
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideJoinDialog() },
            title = { Text("Join Session") },
            text = {
                Column {
                    Text("Enter the session ID")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = sesionIdInput,
                        onValueChange = { sesionIdInput = it },
                        label = { Text("Session ID") },
                        placeholder = { Text("Ex: abc123def456") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (joinDialogMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = joinDialogMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (joinDialogMessage.contains("Error") || joinDialogMessage.contains("Error")) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sesionIdInput.isNotBlank()) {
                            viewModel.joinSesion(sesionIdInput, context)
                        }
                    },
                    enabled = sesionIdInput.isNotBlank() && joinDialogMessage.isEmpty()
                ) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.hideJoinDialog()
                        sesionIdInput = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
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
                FloatingActionButton(
                    onClick = {
                        viewModel.showJoinDialog()
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.GroupAdd, "Join session")
                }
                FloatingActionButton(
                    onClick = {
                        onNavigateToCreator()
                    }
                ) {
                    Icon(Icons.Default.Add, "Create Session")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "My sessions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            // Email usuario
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // sesiones
            if (!isLoading && sesiones.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${sesiones.size} sesión${if (sesiones.size != 1) "es" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Botón de recargar
                    IconButton(
                        onClick = { viewModel.loadSesiones(context) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Recargar sesiones",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading Sessions...")
                    }
                }
            } else if (error.isNotEmpty()) {
                //error
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
                            text = "You aren´t in any session",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Join a session or create a new one",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            Button(
                                onClick = { viewModel.showJoinDialog() }
                            ) {
                                Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Join a session")
                            }

                            Button(
                                onClick = { onNavigateToCreator() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create a session")
                            }
                        }
                    }
                }
            } else {
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
                } else if (sesion.jugadores.contains(currentUserEmail)) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "PLAYER",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                            "YOU"
                        } else {
                            sesion.masterNombre ?: "Loading..."
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


                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ID:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = sesion.id.take(12) + if (sesion.id.length > 12) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                sesion.fechaCreacion?.let { fecha ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Created:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fecha.toDate().toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
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

