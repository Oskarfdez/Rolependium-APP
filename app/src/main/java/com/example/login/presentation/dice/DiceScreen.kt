package com.example.login.presentation.dice

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(
    onNavigateToList: () -> Unit,
    onNavigateToDice: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    email: String
) { val viewModel: DiceViewModel = viewModel()
    val context = LocalContext.current

    // Estados observados del ViewModel
    val diceValue by viewModel.diceValue.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val rollHistory by viewModel.rollHistory.collectAsState()

    // Estado local para el cooldown
    var cooldownRemaining by remember { mutableStateOf(0) }
    val canRoll = !isRolling && cooldownRemaining == 0

    // Efecto para manejar el cooldown visual
    LaunchedEffect(isRolling) {
        if (isRolling) {
            // Cuando empieza a rodar, iniciar cooldown de 2 segundos
            cooldownRemaining = 2000
            while (cooldownRemaining > 0) {
                delay(100L)
                cooldownRemaining -= 100
            }
            cooldownRemaining = 0
        }
    }

    // Configurar el detector de agitación
    LaunchedEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val shakeDetector = ShakeDetector {
            if (canRoll) {
                viewModel.rollDice()
            }
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            shakeDetector,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
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
            Row {

                FloatingActionButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.padding(end = 8.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Icon(Icons.Default.Clear, "Limpiar historial")
                    }

                FloatingActionButton(
                        onClick = { viewModel.rollDice() }
                    ) {
                        Icon(Icons.Default.Refresh, "Rodar dado")
                    }

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Display del dado
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRolling) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Rodando...",
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = diceValue.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (cooldownRemaining > 0) {
                                Text(
                                    text = "Listo en: ${cooldownRemaining / 1000 + 1}s",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }

            // Información del estado
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isRolling) {
                    Text(
                        text = "Rodando el dado...",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Agita el dispositivo o presiona el botón",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Historial de tiradas
            if (rollHistory.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Historial de tiradas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(rollHistory.reversed()) { roll ->
                            HistoryItem(roll = roll)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(roll: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tirada:")
            Text(
                text = roll.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Preview
@Composable
fun DiceScreenPreview() {
    DiceScreen(
        onNavigateToList = {},
        onNavigateToDice = {},
        onNavigateToUser = {},
        onNavigateToHome = {},
        onBack = {},
        email = "")
}