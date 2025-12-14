package com.example.login.presentation.spells

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.login.ui.theme.LoginTheme
import com.example.login.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellsScreen(
    email: String,
    onNavigateToList: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDice: () -> Unit,
    onNavigateToSpells: () -> Unit,
    onNavigateToSpellInfo: (
        name: String,
        level: String,
        school: String,
        classes: String,
        description: String
    ) -> Unit,  // <-- Nuevo parámetro
    onBack: () -> Unit
) {
    val viewModel: SpellsViewModel = viewModel()
    val context = LocalContext.current
    val spells by viewModel.spells.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Mapa de imágenes para cada escuela de conjuros
    val spellSchoolImages = mapOf(
        "Abjuration" to R.drawable.abjuracion,
        "Conjuration" to R.drawable.conjuracion,
        "Divination" to R.drawable.adivinacion,
        "Enchantment" to R.drawable.encantamiento,
        "Evocation" to R.drawable.evocacion,
        "Illusion" to R.drawable.ilusion,
        "Necromancy" to R.drawable.nigromancia,
        "Transmutation" to R.drawable.transmutacion
    )

    // Estados para los filtros
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<Int?>(null) }
    var selectedSchool by remember { mutableStateOf<String?>(null) }
    var selectedClass by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    // Cargar conjuros al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadSpells(context)
    }

    // Filtrar los conjuros basado en los filtros activos
    val filteredSpells = remember(spells, searchQuery, selectedLevel, selectedSchool, selectedClass) {
        spells.filter { spell ->
            // Filtro por búsqueda de nombre
            val matchesSearch = searchQuery.isEmpty() ||
                    spell.nombre.contains(searchQuery, ignoreCase = true)

            // Filtro por nivel
            val matchesLevel = selectedLevel == null || spell.nivel == selectedLevel

            // Filtro por escuela
            val matchesSchool = selectedSchool == null ||
                    spell.escuela.equals(selectedSchool, ignoreCase = true)

            // Filtro por clase
            val matchesClass = selectedClass == null ||
                    spell.clases.contains(selectedClass!!, ignoreCase = true)

            matchesSearch && matchesLevel && matchesSchool && matchesClass
        }
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
            if (!isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón de filtros
                    FloatingActionButton(
                        onClick = { showFilters = !showFilters },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = if (showFilters) "Hide filters" else "Show filters"
                        )
                    }
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
                text = "Spell List",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search by name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            // Panel de filtros (condicional)
            if (showFilters) {
                FilterPanel(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { level ->
                        selectedLevel = if (selectedLevel == level) null else level
                    },
                    selectedSchool = selectedSchool,
                    onSchoolSelected = { school ->
                        selectedSchool = if (selectedSchool == school) null else school
                    },
                    selectedClass = selectedClass,
                    onClassSelected = { spellClass ->
                        selectedClass = if (selectedClass == spellClass) null else spellClass
                    },
                    onClearFilters = {
                        selectedLevel = null
                        selectedSchool = null
                        selectedClass = null
                    },
                    spellSchoolImages = spellSchoolImages
                )
            }

            // Indicadores de filtros activos
            ActiveFiltersRow(
                selectedLevel = selectedLevel,
                selectedSchool = selectedSchool,
                selectedClass = selectedClass,
                onRemoveLevel = { selectedLevel = null },
                onRemoveSchool = { selectedSchool = null },
                onRemoveClass = { selectedClass = null },
                spellSchoolImages = spellSchoolImages
            )

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
                        Text("Loading spells...")
                    }
                }
            } else if (filteredSpells.isEmpty()) {
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
                            Icons.Default.SearchOff,
                            contentDescription = "No results",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedLevel != null ||
                                selectedSchool != null || selectedClass != null) {
                                "No spells found with the applied filters"
                            } else {
                                "No spells found"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        if (searchQuery.isNotEmpty() || selectedLevel != null ||
                            selectedSchool != null || selectedClass != null) {
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedLevel = null
                                    selectedSchool = null
                                    selectedClass = null
                                }
                            ) {
                                Text("Clear all filters")
                            }
                        }
                    }
                }
            } else {
                // Contador de resultados
                Text(
                    text = "${filteredSpells.size} spell${if (filteredSpells.size != 1) "s" else ""} found",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Lista de conjuros
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSpells) { spell ->
                        CartaItem(
                            spell = spell,
                            onNavigateToSpellInfo = onNavigateToSpellInfo,
                            spellSchoolImages = spellSchoolImages
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    selectedLevel: Int?,
    onLevelSelected: (Int) -> Unit,
    selectedSchool: String?,
    onSchoolSelected: (String) -> Unit,
    selectedClass: String?,
    onClassSelected: (String) -> Unit,
    onClearFilters: () -> Unit,
    spellSchoolImages: Map<String, Int>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(//Columna Scrolleable
            modifier = Modifier.padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con botón para limpiar filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onClearFilters) {
                    Text("Clear all")
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outlineVariant
            )

            // Filtro por nivel
            Text(
                text = "Level",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                for (level in 0..9) {
                    val isSelected = selectedLevel == level
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLevelSelected(level) },
                        label = {
                            Text(
                                if (level == 0) "Cantrip" else level.toString(),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            disabledSelectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            // Filtro por escuela
            Text(
                text = "School",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                val schools = listOf(
                    "Abjuration",
                    "Conjuration",
                    "Divination",
                    "Enchantment",
                    "Evocation",
                    "Illusion",
                    "Necromancy",
                    "Transmutation"
                )

                schools.forEach { school ->
                    val isSelected = selectedSchool == school
                    val schoolImage = spellSchoolImages.getOrDefault(school, R.drawable.scroll_24)

                    FilterChip(
                        selected = isSelected,
                        onClick = { onSchoolSelected(school) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = schoolImage),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    school,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            disabledSelectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            // Filtro por clase
            Text(
                text = "Class",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val classes = listOf(
                    "Bard",
                    "Cleric",
                    "Druid",
                    "Sorcerer",
                    "Warlock",
                    "Wizard",
                    "Paladin",
                    "Ranger"
                )

                classes.forEach { spellClass ->
                    val isSelected = selectedClass == spellClass
                    FilterChip(
                        selected = isSelected,
                        onClick = { onClassSelected(spellClass) },
                        label = {
                            Text(
                                spellClass,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            disabledSelectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveFiltersRow(
    selectedLevel: Int?,
    selectedSchool: String?,
    selectedClass: String?,
    onRemoveLevel: () -> Unit,
    onRemoveSchool: () -> Unit,
    onRemoveClass: () -> Unit,
    spellSchoolImages: Map<String, Int>
) {
    val hasFilters = selectedLevel != null || selectedSchool != null || selectedClass != null

    if (hasFilters) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Active filters:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            selectedLevel?.let {
                SuggestionChip(
                    onClick = onRemoveLevel,
                    label = {
                        Text("Level: ${if (it == 0) "Cantrip" else it}")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove level filter",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            selectedSchool?.let {
                val schoolImage = spellSchoolImages.getOrDefault(it, R.drawable.scroll_24)
                SuggestionChip(
                    onClick = onRemoveSchool,
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = schoolImage),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(it)
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove school filter",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            selectedClass?.let {
                SuggestionChip(
                    onClick = onRemoveClass,
                    label = { Text(it) },
                    icon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove class filter",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CartaItem(
    spell: SpellsState,
    onNavigateToSpellInfo: (
        name: String,
        level: String,
        school: String,
        classes: String,
        description: String
    ) -> Unit,
    spellSchoolImages: Map<String, Int>
) {
    val spellImage = spellSchoolImages.getOrDefault(spell.escuela, R.drawable.scroll_24)

    Card(
        modifier = Modifier.fillMaxWidth().
        clickable( onClick = { onNavigateToSpellInfo(
            spell.nombre,
            spell.nivel.toString(),
            spell.escuela,
            spell.clases,
            spell.descripcion
        ) }),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge del nivel
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        text = if (spell.nivel == 0) "Cantrip" else "Lvl ${spell.nivel}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Nombre del conjuro
                Text(
                    text = spell.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información en filas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Escuela con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = spellImage),
                        contentDescription = "${spell.escuela} school icon",
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "School",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = spell.escuela,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Clases
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Classes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = spell.clases,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}


