package com.example.login.presentation.chardata

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CharDataViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val state = mutableStateOf<CharDataState?>(null)

    fun leerDatosPorId(
        personajeId: String,
        onError: (Exception) -> Unit
    ) {
        db.collection("personajes")
            .document(personajeId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    try {
                        val nombre = doc.getString("name") ?: ""
                        val clase = doc.getString("class") ?: ""
                        val hitpoints = doc.getLong("hitpoints")?.toInt() ?: 0
                        val email = doc.getString("email") ?: ""
                        val proficiencia = (doc.getLong("proficiencia") ?: 2).toInt()

                        val caracteristicas = (doc.get("caracteristicas") as? Map<String, Long>)
                            ?.mapValues { it.value.toInt() } ?: emptyMap()

                        val habilidades = (doc.get("habilidades") as? Map<String, Long>)
                            ?.mapValues { it.value.toInt() } ?: emptyMap()

                        val armorClass = doc.getLong("armorClass")?.toInt() ?: 0

                        val salvaciones = (doc.get("salvaciones") as? Map<String, Boolean>) ?: emptyMap()

                        val notas = doc.getString("notas") ?: ""

                        state.value = CharDataState(
                            nombre = nombre,
                            clase = clase,
                            hitpoints = hitpoints,
                            caracteristicas = caracteristicas,
                            proficiencia = proficiencia,
                            habilidades = habilidades,
                            salvaciones = salvaciones,
                            armorClass = armorClass,
                            email = email,
                            notas = notas
                        )
                    } catch (e: Exception) {
                        onError(e)
                    }
                } else {
                    onError(Exception("Documento no encontrado"))
                }
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    fun setEmail(email: String) {
        state.value = state.value?.copy(email = email)
    }


}