package com.example.login.presentation.charlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val personajeList = MutableStateFlow<List<CharListState>>(emptyList())
    val characters: StateFlow<List<CharListState>> = personajeList

    fun cargarPersonajes(email: String) {
        viewModelScope.launch {
            db.collection("personajes")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.documents.map { doc ->
                        CharListState(
                            id = doc.id,
                            nombre = doc.getString("name") ?: "Desconocido",
                            clase = doc.getString("class") ?: "Sin clase"
                        )
                    }
                    personajeList.value = lista
                }
                .addOnFailureListener { e ->
                    println("Error cargando guerreros: ${e.message}")
                }
        }
    }

    fun eliminarPersonaje(id: String) {
        viewModelScope.launch {
            db.collection("personajes")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    // Actualizar la lista local eliminando el personaje
                    personajeList.value = personajeList.value.filter { it.id != id }
                    println("Personaje eliminado exitosamente")
                }
                .addOnFailureListener { e ->
                    println("Error eliminando personaje: ${e.message}")
                }
        }
    }
}