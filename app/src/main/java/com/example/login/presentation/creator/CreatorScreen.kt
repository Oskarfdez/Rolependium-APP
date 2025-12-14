package com.example.login.presentation.creator

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.floor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun CreatorScreen(
    email: String,
    onNavigateToList: () -> Unit,
) {
    val viewModel: CreatorViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var charclass by remember { mutableStateOf("Warrior") }
    val classlist = listOf(
        "Warrior", "Wizard", "Ranger", "Rogue", "Cleric", "Paladin",
        "Monk", "Warlock", "Barbarian", "Sorcerer", "Bard", "Druid"
    )
    val skillsperability = mapOf(
        "Strength" to listOf("Athletics"),
        "Dexterity" to listOf("Acrobatics", "Sleight of Hand", "Stealth"),
        "Intelligence" to listOf("Arcana", "History", "Investigation", "Nature", "Religion"),
        "Wisdom" to listOf("Perception", "Insight", "Medicine", "Survival", "Animal Handling"),
        "Charisma" to listOf("Deception", "Performance", "Intimidation", "Persuasion")
    )
    val ability = remember { mutableStateListOf(8, 8, 8, 8, 8, 8) }
    val saving = remember { mutableStateListOf(false, false, false, false, false, false) }
    var profValue by remember { mutableStateOf("2") }
    val proficiencies = remember { mutableStateMapOf<String, Boolean>() }
    var baseAC by remember { mutableStateOf("10") }
    val dexmodifier = (ability[1] - 10) / 2
    var armorClass = (baseAC.toIntOrNull() ?: 0) + dexmodifier
    var hitpoints by remember { mutableIntStateOf(0) }
    var notas by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Character Creator",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp // Control de altura de línea
                    )
                    Text(
                        text = "Create your adventurer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // BASIC INFO
            ElegantCard(
                title = "Basic Info",
                icon = Icons.Default.ArrowDropDown
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    NombrePersonajeField(nombre = name, onNombreChange = { name = it })

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClaseDropdown(
                            classes = classlist,
                            selected = charclass,
                            modifier = Modifier.weight(1f),
                            onClassSelected = { charclass = it }
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            CompraPuntosText(viewModel)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HitPointsField(
                            value = hitpoints.toString(),
                            onValueChange = { hitpoints = it.toIntOrNull() ?: 0 },
                            modifier = Modifier.weight(1f)
                        )

                        ProficienciaField(
                            value = profValue,
                            onValueChange = { profValue = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ATTRIBUTES
            ElegantCard(
                title = "Attributes",
                icon = Icons.Default.ArrowDropUp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CaracteristicasTable(
                        caracteristicas = ability,
                        viewModel = viewModel,
                        proficiencia = profValue.toIntOrNull() ?: 2,
                        salvaciones = saving
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ARMOR AND SKILLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ARMOR CLASS
                ElegantCard(
                    title = "ArmorClass",
                    modifier = Modifier.weight(1f),
                    showIcon = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ArmorClassField(
                            baseAC = baseAC,
                            onBaseACChange = { baseAC = it },
                            modDestreza = dexmodifier,
                            totalAC = armorClass
                        )
                    }
                }

                // SKILLS
                ElegantCard(
                    title = "Skills",
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(max = 380.dp),
                    showIcon = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        HabilidadesRow(
                            caracteristicas = ability,
                            proficiencia = profValue.toIntOrNull() ?: 0,
                            competencias = proficiencies,
                            listaHabilidades = skillsperability
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // NOTES
            ElegantCard(
                title = "Notes",
                showIcon = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    NotasField(
                        value = notas,
                        onValueChange = { notas = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ACTION BUTTONS
            AccionButtons(
                onLimpiar = {
                    name = ""
                    charclass = "Warrior"
                    for (i in ability.indices) ability[i] = 8
                    profValue = "2"
                    proficiencies.clear()
                    for (i in saving.indices) saving[i] = false
                    viewModel.reiniciarpuntos()
                    baseAC = "10"
                    hitpoints = 0
                    notas = ""
                },
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
                        notas = notas,
                        onSuccess = {
                            Log.d("Character", "Saved successfully")
                        },
                        onError = {
                            Log.e("Character", "Error saving: ${it.message}")
                        }
                    )
                    name = ""
                    charclass = "Warrior"
                    for (i in ability.indices) ability[i] = 8
                    profValue = "2"
                    proficiencies.clear()
                    for (i in saving.indices) saving[i] = false
                    viewModel.reiniciarpuntos()
                    baseAC = "10"
                    hitpoints = 0
                    notas = ""
                    onNavigateToList()
                }
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ElegantCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    showIcon: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    lineHeight = 20.sp
                )

                if (showIcon && icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun CaracteristicasTable(
    caracteristicas: SnapshotStateList<Int>,
    viewModel: CreatorViewModel,
    proficiencia: Int,
    salvaciones: SnapshotStateList<Boolean>
) {
    val caractList = listOf("Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma")

    for (row in 0 until 3) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (col in 0 until 2) {
                val index = row * 2 + col

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    tonalElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = caractList[index],
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp,
                            maxLines = 1,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
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
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Text("-", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }

                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 1.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = caracteristicas[index].toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 24.sp
                                    )
                                }
                            }

                            IconButton(
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
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            ) {
                                Text("+", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val baseMod = floor((caracteristicas[index] - 10).toDouble() / 2).toInt()
                        val modText = if (baseMod >= 0) "+$baseMod" else "$baseMod"

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = "Mod: $modText",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val totalMod = baseMod + if (salvaciones[index]) proficiencia else 0
                            val salvText = if (totalMod >= 0) "+$totalMod" else "$totalMod"

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (salvaciones[index])
                                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                    ),
                                color = Color.Transparent
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "Save.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Checkbox(
                                        checked = salvaciones[index],
                                        onCheckedChange = { salvaciones[index] = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.tertiary,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        salvText,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (salvaciones[index])
                                            MaterialTheme.colorScheme.tertiary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
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
        "Strength" to 0,
        "Dexterity" to 1,
        "Intelligence" to 3,
        "Wisdom" to 4,
        "Charisma" to 5
    )

    Column {
        listaHabilidades.forEach { (caract, habilidades) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 0.dp,
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = caract,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp),
                        maxLines = 1,
                        lineHeight = 18.sp
                    )

                    habilidades.forEach { habilidad ->
                        val mod = (caracteristicas[caractIndex[caract] ?: 0] - 10) / 2
                        val tieneCompetencia = competencias.getOrDefault(habilidad, false)
                        val total = mod + if (tieneCompetencia) proficiencia else 0

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = if (tieneCompetencia)
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                            tonalElevation = if (tieneCompetencia) 0.dp else 0.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Checkbox(
                                    checked = tieneCompetencia,
                                    onCheckedChange = { competencias[habilidad] = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.tertiary,
                                        uncheckedColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = habilidad,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    lineHeight = 16.sp
                                )

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (tieneCompetencia)
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        ),
                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = if (total >= 0) "+$total" else "$total",
                                        color = if (tieneCompetencia)
                                            MaterialTheme.colorScheme.tertiary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (tieneCompetencia) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompraPuntosText(viewModel: CreatorViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Purchase Points",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.3.sp,
                maxLines = 1,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${viewModel.state.value.compranums}/27",
                style = MaterialTheme.typography.titleMedium,
                color = viewModel.state.value.colortext ?: MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Progress bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(viewModel.state.value.compranums / 27f)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun AccionButtons(onLimpiar: () -> Unit, onEnviar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onLimpiar,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Clear",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 18.sp
            )
        }

        Button(
            onClick = onEnviar,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 18.sp
            )
        }
    }
}


@Composable
fun ProficienciaField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                "Proficiency bonus",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun HitPointsField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                "Hit Points",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ArmorClassField(
    baseAC: String,
    onBaseACChange: (String) -> Unit,
    modDestreza: Int,
    totalAC: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = baseAC,
            onValueChange = onBaseACChange,
            label = { Text("Base AC") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 3.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "AC",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = totalAC.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = baseAC.ifEmpty { "0" },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = if (modDestreza >= 0) "+$modDestreza" else "$modDestreza",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
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
        label = {
            Text(
                "Character notes",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        placeholder = {
            Text(
                "Write your notes here...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
    )}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClaseDropdown(
    classes: List<String>,
    selected: String,
    modifier: Modifier = Modifier,
    onClassSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = {
                Text(
                    "Class",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            classes.forEach { cls ->
                DropdownMenuItem(
                    text = {
                        Text(
                            cls,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onClassSelected(cls)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (selected == cls) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}

@Composable
fun NombrePersonajeField(
    nombre: String,
    onNombreChange: (String) -> Unit
) {
    OutlinedTextField(
        value = nombre,
        onValueChange = onNombreChange,
        label = {
            Text(
                "Character name",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Preview (showBackground = true)
@Composable
fun ArmorClassPreview(){
    ArmorClassField(
        baseAC = "10",
        onBaseACChange = {},
        modDestreza = 2,
        totalAC = 15
    )
}