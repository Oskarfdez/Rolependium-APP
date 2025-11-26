package com.example.login.presentation.chardata

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.login.R
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import com.example.login.ui.theme.ROLERED
import kotlin.math.floor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonajeDataScreen(
    charId: String,
    viewModel: CharDataViewModel = hiltViewModel(),
    onNavigateToUser: () -> Unit,
    onBack: () -> Unit
) {
    val personajeState = viewModel.state.value
    val imageabilityes = listOf(
        R.drawable.strenth,
        R.drawable.dexterity,
        R.drawable.constitution,
        R.drawable.intelligence,
        R.drawable.wisdom,
        R.drawable.charisma)


    LaunchedEffect(charId) {
        viewModel.leerDatosPorId(charId) {
            println("❌ Error al cargar personaje: ${it.message}")
        }
    }

    Scaffold(
        topBar = { TopBar(onNavigateToUser, onBack) },

    ){
        if (personajeState != null) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp))
            {
                Spacer(modifier = Modifier.height(55.dp))

                Text(
                    text = personajeState.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Clase: ${personajeState.clase}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconText(image = R.drawable.heart, text = personajeState.hitpoints.toString(),"P.de Golpe")
                    IconText(image = R.drawable.shield, text = personajeState.armorClass.toString(), "C.de Armadura")
                    IconText(image = R.drawable.stop, text = personajeState.proficiencia.toString(),"Proficiencia")
                }


                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        personajeState.caracteristicas.entries.forEachIndexed { index, (clave, valor) ->
                            AbilityIcon(
                                image = imageabilityes.getOrNull(index) ?: R.drawable.shield,
                                text = clave,
                                ability = valor.toString()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Habilidades",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        personajeState.habilidades.forEach { (clave, valor) ->
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    val masmenosvalor = if (valor >= 0) "+$valor" else "$valor"
                                    Text(
                                        text = "- $clave: $masmenosvalor",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                val caracterNum = personajeState.caracteristicas.values.toList()
                SalvacionesGrid(personajeState.salvaciones, caracterNum, personajeState.proficiencia)

                Spacer(modifier = Modifier.height(24.dp))
                Text("Notas del jugador", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                NotesField(notas = personajeState.notas)

                Spacer(modifier = Modifier.height(24.dp))

            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }


}

@Composable
fun AbilityIcon(
    image: Int,
    text: String,
    ability: String,
) {
    val abilityInt = ability.toInt()
    val modificador = floor((abilityInt - 10).toDouble() / 2).toInt()
    val modText = if (modificador >= 0) "+$modificador" else "$modificador"

    Box(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(minWidth = 100.dp)
                .wrapContentHeight()
                .width(140.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = "ICONO",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(8.dp)
                )
                Text(text = text, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = ability,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = modText,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}


@Composable
fun SalvacionesGrid(
    salvaciones: Map<String, Boolean>,
    caracterNum: List<Int>,
    proficiencia: Int,
) {
    val items = salvaciones.entries.toList()
    val rows = items.chunked(3)

    Column {
        Text(
            text = "Salvaciones:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEachIndexed { colIndex, (clave, valor) ->
                    val index = rowIndex * 3 + colIndex
                    val baseValor = caracterNum.getOrNull(index) ?: 0
                    val mod = floor((baseValor - 10).toDouble() / 2).toInt()
                    val modprof = if (valor) mod + proficiencia else mod
                    val modText = if (modprof >= 0) "+$modprof" else "$modprof"

                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(text = clave)
                            Text(text = if (valor) "✅" else "❌")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Mod: $modText")
                        }
                    }
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}




@Composable
fun IconText(image: Int, text: String, name: String) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Icono",
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun NotesField(
    notas: String
) {
    TextField(
        value = notas,
        onValueChange = {}, // sin efecto
        enabled = false,
        label = { Text("Notas") },
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
        colors = TextFieldDefaults.colors(
            disabledContainerColor = Color.LightGray,
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.DarkGray,
            disabledIndicatorColor = Color.Transparent
        )
    )
}


