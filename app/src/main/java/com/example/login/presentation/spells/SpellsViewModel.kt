package com.example.login.presentation.spells

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpellsViewModel: ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    fun setEmail(email: String) {
        _email.value = email
    }

    private val _spells = MutableStateFlow<List<SpellsState>>(emptyList())
    val spells: StateFlow<List<SpellsState>> = _spells.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSpells(context: android.content.Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val spellsCargadas = withContext(Dispatchers.IO) {
                    readSpells(context)
                }
                _spells.value = spellsCargadas
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun readSpells(context: android.content.Context): List<SpellsState> {
        val spells = mutableListOf<SpellsState>()

        try {
            context.resources.openRawResource(R.raw.spells).use { inputStream ->
                inputStream.bufferedReader().useLines { lines ->
                    var isFirstLine = true
                    lines.forEach { line ->
                        if (isFirstLine) {
                            isFirstLine = false
                            return@forEach // Saltar cabecera
                        }

                        // Usar una expresión regular para dividir por comas que no estén dentro de comillas
                        val valores = parseCsvLine(line)

                        if (valores.size >= 5) {
                            // Limpiar las clases: eliminar comillas y espacios extra
                            val clasesLimpias = cleanClassesField(valores[3].trim())

                            val spell = SpellsState(
                                nombre = valores[0].trim(),
                                escuela = valores[1].trim(),
                                nivel = valores[2].trim().toIntOrNull() ?: 0,
                                clases = clasesLimpias,
                                descripcion = valores[4].trim()
                            )
                            spells.add(spell)
                        } else if (valores.size >= 4) {
                            // Caso alternativo si no hay descripción
                            val clasesLimpias = cleanClassesField(valores[3].trim())

                            val spell = SpellsState(
                                nombre = valores[0].trim(),
                                escuela = valores[1].trim(),
                                nivel = valores[2].trim().toIntOrNull() ?: 0,
                                clases = clasesLimpias,
                                descripcion = ""
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

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var insideQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]

            when {
                c == '"' -> {
                    // Si estamos dentro de comillas y el siguiente caracter es también una comilla
                    // (comillas escapadas), agregamos una comilla al valor
                    if (insideQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i += 2 // Saltar ambas comillas
                        continue
                    } else {
                        insideQuotes = !insideQuotes
                    }
                }
                c == ',' && !insideQuotes -> {
                    // Fin de un campo, agregar al resultado
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> {
                    current.append(c)
                }
            }
            i++
        }

        // Agregar el último campo
        result.add(current.toString())

        return result
    }

    private fun cleanClassesField(classes: String): String {
        var cleaned = classes.trim()

        // Eliminar comillas al inicio y al final si existen
        if (cleaned.startsWith('"') && cleaned.endsWith('"')) {
            cleaned = cleaned.substring(1, cleaned.length - 1)
        }

        // Eliminar comillas individuales si quedan
        cleaned = cleaned.replace("\"", "")

        // Normalizar espacios después de comas
        cleaned = cleaned.replace("\\s*,\\s*".toRegex(), ", ")

        // Eliminar espacios extra al inicio y final
        cleaned = cleaned.trim()

        return cleaned
    }}

