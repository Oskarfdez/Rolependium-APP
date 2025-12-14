package com.example.login.presentation.dice
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import kotlinx.coroutines.delay
import com.example.login.R

// Data class para representar una tirada completa
data class DiceRoll(
    val diceValue: Int,
    val modifier: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val total: Int get() = diceValue + modifier
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(
    email: String,
    onNavigateToList: () -> Unit,
    onNavigateToDice: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSpells: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: DiceViewModel = viewModel()
    val context = LocalContext.current
    var currentModifier by remember { mutableIntStateOf(0) }

    // ViewModel
    val currentDiceValue by viewModel.diceValue.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val rollHistory by viewModel.rollHistory.collectAsState()

    var cooldownRemaining by remember { mutableStateOf(0) }
    val canRoll = !isRolling && cooldownRemaining == 0

    var specialBackgroundColor by remember { mutableStateOf<Color?>(null) }
    var showSpecialBackground by remember { mutableStateOf(false) }


    LaunchedEffect(isRolling) {
        if (isRolling) {
            cooldownRemaining = 2000
            while (cooldownRemaining > 0) {
                delay(100L)
                cooldownRemaining -= 100
            }

            cooldownRemaining = 0

            when (currentDiceValue) {
                20 -> {
                    showSpecialBackground = true
                    specialBackgroundColor = Color.Green.copy(alpha = 0.3f)
                    delay(1000)


                    specialBackgroundColor = null
                    delay(300)
                    showSpecialBackground = false
                }
                1 -> {
                    showSpecialBackground = true
                    specialBackgroundColor = Color.Red.copy(alpha = 0.3f)
                    delay(1000)


                    specialBackgroundColor = null
                    delay(300)
                    showSpecialBackground = false
                }
            }
        }
    }

    // Detector de Agitacion
    LaunchedEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val shakeDetector = ShakeDetector {
            if (canRoll) {
                viewModel.rollDice(currentModifier)
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
        topBar = { TopBar(onNavigateToUser, onBack) },
        bottomBar = {
            BottomBar(
                onNavigateToDice,
                onNavigateToList,
                onNavigateToHome,
                onNavigateToSpells
            )
        },
        floatingActionButton = {
            Row {
                FloatingActionButton(
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier.padding(end = 8.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(Icons.Default.Clear, "Clear history")
                }

                FloatingActionButton(
                    onClick = { viewModel.rollDice(currentModifier) }
                ) {
                    Icon(Icons.Default.Refresh, "Roll dice")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (showSpecialBackground && specialBackgroundColor != null)
                        specialBackgroundColor!!
                    else
                        MaterialTheme.colorScheme.background
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .size(220.dp)
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
                                    text = "Rolling...",
                                    fontSize = 14.sp
                                )
                            }
                        } else {

                            val displayValue = if (currentDiceValue > 0) {
                                currentDiceValue + currentModifier
                            } else {
                                0
                            }

                            IconText(
                                image = R.drawable.dice,
                                text = displayValue.toString(),
                                name = if (currentDiceValue == 20) "CRITICAL"
                                else if (currentDiceValue == 1) "FAILURE"
                                else "DICE",
                                textColor = if (currentDiceValue == 20) {
                                    Color.Green
                                } else if (currentDiceValue == 1) {
                                    Color.Red
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentModifier != 0 && currentDiceValue > 0) {
                        Text(
                            text = "($currentDiceValue + $currentModifier)",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = currentModifier.toString(),
                        onValueChange = { currentModifier = it.toIntOrNull() ?: 0 },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("MODIFIER") },
                        singleLine = true,
                        modifier = Modifier.width(200.dp)
                    )
                }

                if (rollHistory.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(4f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Roll History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(rollHistory.reversed()) { diceRoll ->
                                HistoryItem(diceRoll = diceRoll)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(diceRoll: DiceRoll) {
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
            Text(
                text = "${diceRoll.diceValue} + ${if (diceRoll.modifier >= 0) diceRoll.modifier.toString() else diceRoll.modifier}",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(20.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = diceRoll.total.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (diceRoll.diceValue == 20) {
                        Color.Green
                    } else if (diceRoll.diceValue == 1) {
                        Color.Red
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}

@Composable
fun IconText(
    image: Int,
    text: String,
    name: String,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Dice icon",
                modifier = Modifier
                    .size(300.dp)
                    .alpha(0.15f),
                alignment = Alignment.Center
            )

            Text(
                text = text,
                style = MaterialTheme.typography.displayLarge,
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 72.sp, // Much larger
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1
        )
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
        onNavigateToSpells = {},
        email = ""
    )
}