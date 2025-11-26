package com.example.login.presentation.spells

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpellsViewModel: ViewModel() {
    private val _spells = MutableStateFlow<List<SpellsState>>(emptyList())
    val spells: StateFlow<List<SpellsState>> = _spells.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSpells(context: android.content.Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val cartasCargadas = withContext(Dispatchers.IO) {
                    leerCSVDesdeAssets(context)
                }
                _spells.value = cartasCargadas
            } catch (e: Exception) {
                // Manejar error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun leerCSVDesdeAssets(context: android.content.Context): List<SpellsState> {
        val spells = mutableListOf<SpellsState>()

        try {
            context.assets.open("spells.csv").use { inputStream ->
                inputStream.bufferedReader().useLines { lines ->
                    var isFirstLine = true
                    lines.forEach { line ->
                        if (isFirstLine) {
                            isFirstLine = false
                            return@forEach // Saltar cabecera
                        }

                        val valores = line.split(",")
                        if (valores.size >= 5) {
                            val spell = SpellsState(
                                nombre = valores[0].trim(),
                                escuela = valores[1].trim(),
                                nivel = valores[2].trim().toIntOrNull() ?: 0,
                                clases = valores[3].trim(),
                                descripcion = valores[4].trim()
                            )
                            spells.add(spell)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return spells
    }
}