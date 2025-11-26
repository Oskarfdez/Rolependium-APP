package com.example.login.presentation.creator

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar

import com.example.login.ui.theme.ROLERED
import kotlin.math.floor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun CreatorScreen( email: String,
                   onNavigateToList: () -> Unit,) {
    val viewModel: CreatorViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var charclass by remember { mutableStateOf("Guerrero") }
    val classlist = listOf("Guerrero", "Mago", "Explorador", "Pícaro", "Clérigo", "Paladín", "Monje", "Brujo", "Bárbaro", "Hechicero", "Bardo", "Druida")
    val ability = remember { mutableStateListOf(8, 8, 8, 8, 8, 8) }
    val saving = remember { mutableStateListOf(false, false, false, false, false, false) }
    val skillsperability  = mapOf(
        "Fuerza" to listOf("Atletismo"),
        "Destreza" to listOf("Acrobacias", "Juego de Manos", "Sigilo"),
        "Inteligencia" to listOf("Arcano", "Historia", "Investigación", "Naturaleza", "Religión"),
        "Sabiduría" to listOf("Percepción", "Perspicacia", "Medicina", "Supervivencia", "T. con Animales"),
        "Carisma" to listOf("Engaño", "Interpretación", "Intimidación", "Persuasión")
    )
    var profValue by remember { mutableStateOf("2") }
    val proficiencies = remember { mutableStateMapOf<String, Boolean>() }
    var baseAC by remember { mutableStateOf("10") } // valor base editable
    val dexmodifier = (ability[1] - 10) / 2 // índice 1 = Destreza
    var armorClass = (baseAC.toIntOrNull() ?: 0) + dexmodifier
    var hitpoints by remember { mutableIntStateOf(0) }
    var notas by remember { mutableStateOf("") }



    Scaffold(
){
    Column( modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {


        //NOMBRE DEL PERSONAJE
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Creador de Personaje",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))
        NombrePersonajeField(name) { name = it }


        //CLASE Y COMPRA DE PUNTOS
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            ClaseDropdown(classlist, charclass) { charclass = it }
            Spacer(modifier = Modifier.width(18.dp))
            CompraPuntosText(viewModel)
        }


        //PUNTOS DE GOLPE Y BONUS PROFICIENCIA
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            HitPointsField(
                value = hitpoints.toString(),
                onValueChange = {
                    hitpoints = it.toIntOrNull() ?: 0
                })
            Spacer(modifier = Modifier.width(50.dp))
            ProficienciaField(
                value = profValue,
                onValueChange = { profValue = it },
            )
        }


        //TABLA DE CARACTERISTICAS
        Spacer(modifier = Modifier.height(16.dp))
        CaracteristicasTable(ability, viewModel, profValue.toIntOrNull() ?: 2, saving)


        //CLASE DE ARMADURA
        Spacer(modifier = Modifier.height(16.dp))
            ArmorClassField(
                baseAC = baseAC,
                onBaseACChange = { baseAC = it },
                modDestreza = dexmodifier,
                totalAC = armorClass
            )


        //TABLA DE HABILIDADES
        Spacer(modifier = Modifier.height(16.dp))
        HabilidadesRow(
            caracteristicas = ability,
            proficiencia = profValue.toIntOrNull() ?: 0,
            competencias = proficiencies,
            listaHabilidades = skillsperability

        )

        //NOTAS
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(16.dp)) {
            NotasField(value = notas, onValueChange = { notas = it })
        }


        //BOTONES DE LIMPIAR Y ENVIAR
        Spacer(modifier = Modifier.height(16.dp))
        AccionButtons(
            onLimpiar = {name = ""
                charclass = "Guerrero"
                for (i in ability.indices) ability[i] = 8
                profValue = "2"
                proficiencies.clear()
                for (i in saving.indices) saving[i] = false
                viewModel.reiniciarpuntos()
                baseAC = "10" },
            onEnviar = {
                viewModel.enviarDatos(
                    nombre = name,
                    clase = charclass,
                    hitpoints = hitpoints,
                    caracteristicas = ability.toList(),
                    salvaciones = saving.toList(),
                    proficiencia = profValue.toIntOrNull() ?: 2,
                    habilidades = proficiencies,
                    email = email,
                    armorClass = armorClass,
                    notas= notas,
                    onSuccess = {
                        Log.d("Personaje", "Guardado con éxito")
                    },
                    onError = {
                        Log.e("Personaje", "Error al guardar: ${it.message}")
                    }
                )
                name = ""
                charclass = "Guerrero"
                    for (i in ability.indices) ability[i] = 8
                    profValue = "2"
                proficiencies.clear()
                    for (i in saving.indices) saving[i] = false
                    viewModel.reiniciarpuntos()
                baseAC = "10"
                onNavigateToList()
            }
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}
}

@Composable
fun NombrePersonajeField(nombre: String, onNombreChange: (String) -> Unit) {
    OutlinedTextField(
        value = nombre,
        onValueChange = onNombreChange,
        label = { Text("Nombre del personaje") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ClaseDropdown(classes: List<String>, selected: String, onClassSelected: (String) -> Unit) {
    DropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        classes = classes,
        selectedClass = selected,
        onClassSelected = onClassSelected
    )
}

@Composable
fun DropdownMenuBox(modifier: Modifier, classes: List<String>, selectedClass: String, onClassSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var icon by remember { mutableStateOf(Icons.Default.ArrowDropDown) }

    Box {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = ROLERED),
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Text(selectedClass)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            classes.forEach { cls ->
                DropdownMenuItem(onClick = {
                    onClassSelected(cls)
                    expanded = false
                }) {
                    Text(cls)
                }
            }
        }
    }
    icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
}

@Composable
fun CompraPuntosText(
    viewModel: CreatorViewModel
){
    Text(text = "Puntos de compra: ${viewModel.state.value.compranums}/27"
        , color = viewModel.state.value.colortext)
}

@Composable
fun CaracteristicasTable(
    caracteristicas: SnapshotStateList<Int>,
    viewModel: CreatorViewModel,
    proficiencia: Int,
    salvaciones: SnapshotStateList<Boolean>
) {
    val caractList = listOf("Fuerza", "Destreza", "Constitución", "Inteligencia", "Sabiduría", "Carisma")

    for (row in 0 until 3) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (col in 0 until 2) {
                val index = row * 2 + col

                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .width(160.dp), // límite visual
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = caractList[index],
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (caracteristicas[index] > 8) {
                                        if (caracteristicas[index] > 13) {
                                            caracteristicas[index] -= 1
                                            viewModel.restados()
                                        } else {
                                            caracteristicas[index] -= 1
                                            viewModel.restapuntos()
                                        }
                                    }
                                },
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ROLERED)
                            ) {
                                Text("-", style = MaterialTheme.typography.bodyMedium)
                            }

                            Text(
                                text = caracteristicas[index].toString(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(36.dp)
                            )

                            Button(
                                onClick = {
                                    if (caracteristicas[index] < 20) {
                                        if (caracteristicas[index] >= 13) {
                                            caracteristicas[index] += 1
                                            viewModel.sumardos()
                                        } else {
                                            caracteristicas[index] += 1
                                            viewModel.sumapuntos()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val baseMod = floor((caracteristicas[index] - 10).toDouble() / 2).toInt()
                        val modText = if (baseMod >= 0) "+$baseMod" else "$baseMod"

                        Text(
                            text = "Mod: $modText",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Salv:", style = MaterialTheme.typography.bodySmall)
                            Checkbox(
                                checked = salvaciones[index],
                                onCheckedChange = { salvaciones[index] = it },
                                modifier = Modifier.size(20.dp)
                            )
                            val totalMod = baseMod + if (salvaciones[index]) proficiencia else 0
                            val salvText = if (totalMod >= 0) "+$totalMod" else "$totalMod"
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(salvText, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }


            }
        }
    }
}




@Composable
fun HabilidadesRow(
    caracteristicas: List<Int>,
    proficiencia: Int,
    competencias: MutableMap<String, Boolean>,
    listaHabilidades: Map<String, List<String>>
) {
    val caractIndex = mapOf(
        "Fuerza" to 0,
        "Destreza" to 1,
        "Inteligencia" to 3,
        "Sabiduría" to 4,
        "Carisma" to 5
    )

    Column {
        listaHabilidades.forEach { (caract, habilidades) ->
            Text(
                text = caract,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            habilidades.forEach { habilidad ->
                val mod = (caracteristicas[caractIndex[caract] ?: 0] - 10) / 2
                val tieneCompetencia = competencias.getOrDefault(habilidad, false)
                val total = mod + if (tieneCompetencia) proficiencia else 0

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Checkbox(
                        checked = tieneCompetencia,
                        onCheckedChange = {
                            competencias[habilidad] = it
                        }
                    )
                    Text(text = habilidad, modifier = Modifier.weight(1f))
                    Text(text = "${if (total >= 0) "+$total" else "$total"}")
                }
            }
        }
    }
}

@Composable
fun ProficienciaField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Bon de competencia") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(150.dp)
    )
}

@Composable
fun HitPointsField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Puntos de Golpe") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(150.dp)
    )
}


@Composable
fun ArmorClassField(
    baseAC: String,
    onBaseACChange: (String) -> Unit,
    modDestreza: Int,
    totalAC: Int
) {
    Column {
        OutlinedTextField(
            value = baseAC,
            onValueChange = onBaseACChange,
            label = { Text("Clase de Armadura") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text("Mod. Destreza: ${if (modDestreza >= 0) "+$modDestreza" else "$modDestreza"}")
        Text("Clase de Armadura: $totalAC")
    }
}


@Composable
fun AccionButtons(onLimpiar: () -> Unit, onEnviar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onLimpiar,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Limpiar")
        }

        Button(
            onClick = onEnviar,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Enviar")
        }
    }
}

@Composable
fun NotasField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Notas") },
        placeholder = { Text("Escribe tus notas aquí...") },
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp),
        maxLines = 10,
        singleLine = false,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions.Default
    )
}



