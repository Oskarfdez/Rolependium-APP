package com.example.login.presentation.sesiondata
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionDataScreen(
    onBack: () -> Unit,
    sesionId: String,
    email: String
) {
    //VIEWMODEL
    val viewModel: SesionDataViewModel = viewModel()
    val clipboard = LocalClipboardManager.current
    var nuevoHorario by remember { mutableStateOf("") }
    val showAddHorarioDialog by viewModel.showAddHorarioDialog.collectAsState()

    val showConfirmDialog by viewModel.showConfirmDialog.collectAsState()

    data class JugadorAEliminar(
        val email: String,
        val nombre: String)

    val showEliminarJugadorDialog = remember { mutableStateOf(false) }
    val jugadorAEliminar = remember { mutableStateOf<JugadorAEliminar?>(null) }

    val showLeaveSessionDialog by viewModel.showLeaveSessionDialog.collectAsState()
    val showDeleteSessionDialog by viewModel.showDeleteSessionDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()
    val horariosSeleccionados by viewModel.horariosSeleccionados.collectAsState()

    LaunchedEffect(sesionId) {
        viewModel.loadSesionData(sesionId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val sesionData by viewModel.sesionData.collectAsState()
    val error by viewModel.error.collectAsState()

    val esMaster = sesionData?.masterEmail == email
    val esJugador = sesionData?.jugadores?.contains(email) == true && !esMaster


    //Confirmacion
    if (showAddHorarioDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddHorarioDialog() },
            title = { Text("Add Date") },
            text = {
                Column {
                    Text("Write the date you want to add:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = nuevoHorario,
                        onValueChange = { nuevoHorario = it },
                        label = { Text("Ex: Monday 15:00-17:00") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoHorario.isNotBlank()) {
                            viewModel.addHorario(sesionId, nuevoHorario)
                            nuevoHorario = ""
                        }
                    },
                    enabled = nuevoHorario.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.hideAddHorarioDialog()
                        nuevoHorario = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideConfirmDialog() },
            title = { Text("Confirm Dates") },
            text = {
                Column {
                    Text("You´re going to accept the following dates:")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (horariosSeleccionados.isEmpty()) {
                        Text(
                            "No Date selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        horariosSeleccionados.forEach { horario ->
                            Text(
                                "• $horario",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Note: This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmarHorariosSeleccionados(sesionId)
                    },
                    enabled = horariosSeleccionados.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideConfirmDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEliminarJugadorDialog.value && jugadorAEliminar.value != null) {
        AlertDialog(
            onDismissRequest = {
                showEliminarJugadorDialog.value = false
                jugadorAEliminar.value = null
            },
            title = { Text("Delete Player") },
            text = {
                Column {
                    Text("Are you sure you want to delete this player?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email: ${jugadorAEliminar.value?.email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        jugadorAEliminar.value?.let { jugador ->
                            viewModel.eliminarJugador(sesionId, jugador.email)
                        }
                        showEliminarJugadorDialog.value = false
                        jugadorAEliminar.value = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEliminarJugadorDialog.value = false
                        jugadorAEliminar.value = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    // DIALOG DE DEJAR SESION SI ERES JUGADOR
    if (showLeaveSessionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLeaveSessionDialog() },
            title = { Text("Leave Session") },
            text = {
                Column {
                    Text("Are you sure you want to leave this session?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You will be removed from the player list and your schedule acceptances will be cleared.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.leaveSession(sesionId, email)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Leave Session")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideLeaveSessionDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG DE ELIMINAR SESION SI ERES MASTER
    if (showDeleteSessionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteSessionDialog() },
            title = { Text("Delete Session") },
            text = {
                Column {
                    Text("Are you sure you want to delete this session?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All session data including players, schedules, and acceptances will be permanently deleted.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSession(sesionId)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete Session")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteSessionDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Session Details",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (esMaster) {
                                viewModel.showDeleteSessionDialog()
                            } else if (esJugador) {
                                viewModel.showLeaveSessionDialog()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (esMaster) Icons.Default.Delete else Icons.Default.ExitToApp,
                            contentDescription = if (esMaster) "Delete Session" else "Leave Session",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (esJugador && sesionData?.listaHorarios?.isNotEmpty() == true) {
                FloatingActionButton(
                    onClick = {
                        viewModel.showConfirmDialog()
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirm Date")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@Box
                }

                if (error.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Intentar de nuevo")
                        }
                    }
                    return@Box
                }

                sesionData?.let { sesion ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = sesion.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "ID: ${sesion.id.take(8)}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(sesion.id))
                                    viewModel.showToast("ID copied")
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy ID",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }


                        if (sesion.descripcion.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = sesion.descripcion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Master: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.dice_24),
                                        contentDescription = "Master",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = sesion.masterNombre,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = sesion.masterEmail,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                val numJugadores = maxOf(0, sesion.jugadores.size - 1)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Players:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ) {
                                        Text(text = "$numJugadores")
                                    }
                                }

                                if (numJugadores > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val jugadoresList = sesion.jugadores.filter { it != sesion.masterEmail }
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 200.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(jugadoresList) { jugadorEmail ->
                                            val jugadorNombre = remember(jugadorEmail) {
                                                derivedStateOf {
                                                    jugadorEmail.substringBefore("@")
                                                }
                                            }.value

                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.sword_24),
                                                        contentDescription = "Player",
                                                        tint = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column(
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = jugadorNombre,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        Text(
                                                            text = jugadorEmail,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    if (jugadorEmail == email) {
                                                        Badge(
                                                            containerColor = MaterialTheme.colorScheme.secondary,
                                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                                        ) {
                                                            Text("TÚ")
                                                        }
                                                    }

                                                    if (esMaster && jugadorEmail != email) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        IconButton(
                                                            onClick = {
                                                                jugadorAEliminar.value = JugadorAEliminar(
                                                                    email = jugadorEmail,
                                                                    nombre = jugadorNombre
                                                                )
                                                                showEliminarJugadorDialog.value = true
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Delete,
                                                                contentDescription = "Eliminar jugador",
                                                                tint = MaterialTheme.colorScheme.error,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay jugadores en esta sesión",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        if (sesion.horarioActual.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Horario actual:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = sesion.horarioActual,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Dates available:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ) {
                                            Text(text = "${sesion.listaHorarios.size}")
                                        }

                                        if (esMaster) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { viewModel.showAddHorarioDialog() },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Añadir horario",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }

                                if (sesion.listaHorarios.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 300.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(sesion.listaHorarios) { horario ->
                                            val aceptaciones = sesion.listaAceptaciones[horario] ?: emptyList()

                                            val todosLosJugadoresAceptaron = if (sesion.jugadoresNoMaster.isNotEmpty()) {
                                                val jugadoresAceptaron = aceptaciones.filter { it != sesion.masterEmail }
                                                jugadoresAceptaron.size >= sesion.jugadoresNoMaster.size
                                            } else {
                                                true
                                            }

                                            val usuarioAcepto = horario in sesion.usuarioHaAceptado

                                            HorarioCard(
                                                horario = horario,
                                                aceptaciones = aceptaciones,
                                                todosLosJugadoresAceptaron = todosLosJugadoresAceptaron,
                                                totalJugadores = sesion.jugadoresNoMaster.size,
                                                usuarioAcepto = usuarioAcepto,
                                                esMaster = esMaster,
                                                esJugador = esJugador,
                                                seleccionado = horario in horariosSeleccionados,
                                                onToggleSeleccion = {
                                                    if (esJugador && !usuarioAcepto) {
                                                        viewModel.toggleHorarioSeleccionado(horario)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (esMaster) {
                                            "No Dates available"
                                        } else {
                                            "The master hasn´t added any dates yet"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    )

                                    if (esMaster) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { viewModel.showAddHorarioDialog() },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Add first date")
                                        }
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                sesion.fechaCreacion?.let { timestamp ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = "Fecha creación",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Creada el: ${formatDate(timestamp.toDate())}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Tu email",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Your email: $email",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (esMaster) {
                                            Text(
                                                text = "Role: MASTER",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else if (esJugador) {
                                            Text(
                                                text = "Role: PLAYER",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (esJugador && sesion.listaHorarios.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Instrucciones",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Rules :",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "1. Sellect the date you want to accept\n" +
                                                "2. Press the floating button to accept the date.\n" +
                                                "3. The dates will check when all players accept that date\n" +
                                                "4. The Master can see if a date is available for all players",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (toastMessage != null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Éxito",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = toastMessage!!,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                } ?: run {
                    if (error.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Sin datos",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "There is no data",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorarioCard(
    horario: String,
    aceptaciones: List<String>,
    todosLosJugadoresAceptaron: Boolean,
    totalJugadores: Int, // <-- Total de jugadores (no masters)
    usuarioAcepto: Boolean,
    esMaster: Boolean,
    esJugador: Boolean,
    seleccionado: Boolean,
    onToggleSeleccion: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (todosLosJugadoresAceptaron) {
            // Color verde cuando todos los jugadores han aceptado
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (todosLosJugadoresAceptaron) {
            CardDefaults.outlinedCardBorder()
        } else {
            null
        },
        tonalElevation = if (todosLosJugadoresAceptaron) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (esJugador && !usuarioAcepto) {
                Checkbox(
                    checked = seleccionado,
                    onCheckedChange = { onToggleSeleccion() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else if (esMaster) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Creado por el master",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = horario,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (todosLosJugadoresAceptaron) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    if (usuarioAcepto) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Horario aceptado",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                if (totalJugadores > 0) {
                    Text(
                        text = "Accepted by: ${aceptaciones.size} to $totalJugadores players",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (aceptaciones.size == totalJugadores) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                } else {
                    Text(
                        text = "There´s no players ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (todosLosJugadoresAceptaron) {
                Spacer(modifier = Modifier.width(8.dp))
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Disponible",
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AVAILABLE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault()).format(date)
}