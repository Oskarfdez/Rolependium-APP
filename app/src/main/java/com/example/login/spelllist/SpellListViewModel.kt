
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.spelllist.Conjuro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class SpellListViewModel : ViewModel() {

    private val _spellList = MutableStateFlow<List<Conjuro>>(emptyList())
    val spellList: StateFlow<List<Conjuro>> = _spellList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadConjurosFromCSV()
    }

    fun loadConjurosFromCSV() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val conjuros = readCSVFromAssets()
                _spellList.value = conjuros
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los conjuros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun readCSVFromAssets(): List<Conjuro> {
        val conjuros = mutableListOf<Conjuro>()

        // Necesitarás proporcionar el InputStream desde tu Activity/Fragment
        // Esta función deberá ser llamada con el InputStream correcto
        return conjuros
    }

    // Función para cargar desde un InputStream (se llamará desde la Activity/Fragment)
    fun loadConjurosFromInputStream(inputStream: java.io.InputStream) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val conjuros = parseCSV(inputStream)
                _spellList.value = conjuros
            } catch (e: Exception) {
                _errorMessage.value = "Error al leer el CSV: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseCSV(inputStream: java.io.InputStream): List<Conjuro> {
        val conjuros = mutableListOf<Conjuro>()

        BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
            var isFirstLine = true

            reader.forEachLine { line ->
                if (isFirstLine) {
                    isFirstLine = false
                    return@forEachLine
                }

                try {
                    // Parsear línea CSV (formato simple separado por comas)
                    val values = parseCSVLine(line)

                    if (values.size >= 5) {
                        val conjuro = Conjuro(
                            nombre = values[0].trim(),
                            escuela = values[1].trim(),
                            nivel = values[2].trim().toIntOrNull() ?: 0,
                            clases = values[3].trim(),
                            descripcion = values[4].trim()
                        )
                        conjuros.add(conjuro)
                    }
                } catch (e: Exception) {
                    // Ignorar líneas con error y continuar
                    e.printStackTrace()
                }
            }
        }

        return conjuros
    }

    private fun parseCSVLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        for (i in line.indices) {
            val c = line[i]

            when {
                c == '"' -> {
                    if (inQuotes && i < line.length - 1 && line[i + 1] == '"') {
                        // Comilla doble dentro de comillas
                        current.append('"')
                        i + 1 // Saltar siguiente comilla
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> {
                    current.append(c)
                }
            }
        }

        result.add(current.toString())
        return result
    }

    // Funciones para filtrar y buscar
    fun filterByLevel(level: Int): List<Conjuro> {
        return _spellList.value.filter { it.nivel == level }
    }

    fun filterBySchool(school: String): List<Conjuro> {
        return _spellList.value.filter { it.escuela.equals(school, ignoreCase = true) }
    }

    fun searchSpells(query: String): List<Conjuro> {
        return if (query.isBlank()) {
            _spellList.value
        } else {
            _spellList.value.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.descripcion.contains(query, ignoreCase = true) ||
                        it.clases.contains(query, ignoreCase = true)
            }
        }
    }

    fun getSpellByName(name: String): Conjuro? {
        return _spellList.value.find { it.nombre.equals(name, ignoreCase = true) }
    }

    fun getSpellsByClass(className: String): List<Conjuro> {
        return _spellList.value.filter {
            it.clases.split(",").any { cls ->
                cls.trim().equals(className, ignoreCase = true)
            }
        }
    }
}