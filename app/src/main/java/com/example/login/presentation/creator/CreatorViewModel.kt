package com.example.login.presentation.creator

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.presentation.login.LoginState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreatorViewModel: ViewModel() {
    val state: MutableState<CreatorState> = mutableStateOf(CreatorState())
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    fun setEmail(email: String) {
        _email.value = email
    }
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre
    fun setNombre(nombre: String) {
        _nombre.value = nombre
    }
    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono
    fun setTelefono(telefono: String) {
        _telefono.value = telefono
    }

   fun restapuntos() {
        if (state.value.compranums > 0) {
            state.value = state.value.copy(compranums = state.value.compranums - 1)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Green)
        }else{
            state.value = state.value.copy(compranums = state.value.compranums - 1)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Red)
        }
   }
    fun restados(){
        if (state.value.compranums > 0) {
            state.value = state.value.copy(compranums = state.value.compranums - 2)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Green)
        }else{
            state.value = state.value.copy(compranums = state.value.compranums - 2)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Red)
        }
    }
    fun sumapuntos() {
        if (state.value.compranums < 27) {
            state.value = state.value.copy(compranums = state.value.compranums + 1)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Green)
        }else{
            state.value = state.value.copy(compranums = state.value.compranums + 1)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Red)

        }
    }
    fun sumardos(){
        if (state.value.compranums < 27) {
            state.value = state.value.copy(compranums = state.value.compranums + 2)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Green)
        }else{
            state.value = state.value.copy(compranums = state.value.compranums + 2)
            state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Red)

        }
    }

    fun reiniciarpuntos(){
        state.value = state.value.copy(compranums = 0)
        state.value = state.value.copy(colortext = androidx.compose.ui.graphics.Color.Green)

    }



    private val db = FirebaseFirestore.getInstance()

    fun enviarDatos(
        nombre: String,
        clase: String,
        hitpoints: Int,
        caracteristicas: List<Int>,
        salvaciones: List<Boolean>,
        proficiencia: Int,
        habilidades: Map<String, Boolean>,
        email: String,
        notas: String,
        armorClass: Int,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val mod = { score: Int -> (score - 10) / 2 }

        val nombresCaracteristicas = listOf("Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma")
        val caracteristicasMap = nombresCaracteristicas.zip(caracteristicas).toMap()

        val habilidadesPorCaracteristica = mapOf(
            "Strength" to listOf("Athletics"),
            "Dexterity" to listOf("Acrobatics", "Sleight of Hand", "Stealth"),
            "Intelligence" to listOf("Arcana", "History", "Investigation", "Nature", "Religion"),
            "Wisdom" to listOf("Perception", "Insight", "Medicine", "Survival", "Animal Handling"),
            "Charisma" to listOf("Deception", "Performance", "Intimidation", "Persuasion")
        )

        val baseMods = caracteristicasMap.mapValues { mod(it.value) }

        val habilidadesCalculadas = mutableMapOf<String, Int>()
        habilidadesPorCaracteristica.forEach { (caract, listaHabs) ->
            val baseMod = baseMods[caract] ?: 0
            listaHabs.forEach { habilidad ->
                val tieneProf = habilidades[habilidad] == true
                habilidadesCalculadas[habilidad] = baseMod + if (tieneProf) proficiencia else 0
            }
        }

        val salvacionMap = nombresCaracteristicas.zip(salvaciones).toMap()

        val characterData = hashMapOf(
            "name" to nombre,
            "class" to clase,
            "hitpoints" to hitpoints,
            "caracteristicas" to caracteristicasMap, // 👈 Con nombres
            "email" to email,
            "armorClass" to armorClass,
            "proficiencia" to proficiencia,
            "habilidades" to habilidadesCalculadas,
            "salvaciones" to salvacionMap,
            "notas" to notas
        )

        viewModelScope.launch {
            db.collection("personajes")
                .add(characterData)
                .addOnSuccessListener { documentRef ->
                    onSuccess(documentRef.id)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        }
    }




}