package com.example.login.presentation.sesion
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SesionViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    private val db = FirebaseFirestore.getInstance()

    // Estados observables
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _sesiones = MutableStateFlow<List<SesionState>>(emptyList())
    val sesiones: StateFlow<List<SesionState>> = _sesiones

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    /**
     * Establece el email del usuario actual
     */
    fun setEmail(email: String) {
        _email.value = email
    }

    /**
     * Carga las sesiones en las que el usuario está
     */
    fun loadSesiones(context: Context) {
        if (_email.value.isEmpty()) {
            _error.value = "Error: No se ha configurado el email del usuario"
            return
        }

        _isLoading.value = true
        _error.value = ""
        _sesiones.value = emptyList()

        viewModelScope.launch {
            try {
                val emailUsuario = _email.value

                // Obtener todas las sesiones
                val allSesiones = db.collection("sesiones")
                    .get()
                    .await()

                val sesionesDelUsuario = mutableListOf<QueryDocumentSnapshot>()

                // Filtrar sesiones donde el usuario es master o está en la lista de jugadores
                for (doc in allSesiones.documents) {
                    val sesion = doc.toObject(Sesion::class.java) ?: continue

                    // Verificar si el usuario es master
                    if (sesion.master == emailUsuario) {
                        sesionesDelUsuario.add(doc as QueryDocumentSnapshot)
                        continue
                    }

                    // Verificar si el usuario está en la lista de jugadores (que ahora son solo emails)
                    val usuarioEnJugadores = sesion.jugadores?.any { jugadorEmail ->
                        val email = jugadorEmail as? String
                        email == emailUsuario
                    } ?: false

                    if (usuarioEnJugadores) {
                        sesionesDelUsuario.add(doc as QueryDocumentSnapshot)
                    }
                }

                // Convertir a SesionState y obtener nombres de master
                val sesionesConNombres = mutableListOf<SesionState>()

                for (doc in sesionesDelUsuario) {
                    val sesion = doc.toObject(Sesion::class.java) ?: continue

                    val masterNombre = if (sesion.master == emailUsuario) {
                        "Tú"
                    } else {
                        getUserName(sesion.master)
                    }

                    // Convertir la lista de jugadores (ahora solo emails) a List<String>
                    val emailsJugadores = sesion.jugadores?.mapNotNull {
                        it as? String
                    } ?: emptyList()

                    val sesionState = SesionState(
                        id = doc.id,
                        nombre = sesion.nombre,
                        descripcion = sesion.descripcion,
                        masterEmail = sesion.master,
                        masterNombre = masterNombre,
                        jugadores = emailsJugadores,
                        horarios = sesion.horarios ?: emptyList(),
                        notificaciones = sesion.notificaciones ?: emptyList(),
                        fechaCreacion = sesion.fechaCreacion
                    )

                    sesionesConNombres.add(sesionState)
                }

                // Ordenar por fecha de creación (más reciente primero)
                val sesionesOrdenadas = sesionesConNombres.sortedByDescending {
                    it.fechaCreacion?.seconds ?: 0
                }

                _sesiones.value = sesionesOrdenadas

            } catch (e: Exception) {
                _error.value = "Error al cargar las sesiones: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene el nombre de usuario desde la colección "usuarios"
     */
    private suspend fun getUserName(email: String): String? {
        return try {
            val doc = db.collection("usuarios")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (!doc.isEmpty) {
                val usuario = doc.documents[0].toObject(Usuario::class.java)
                usuario?.nombre ?: email
            } else {
                email
            }
        } catch (e: Exception) {
            e.printStackTrace()
            email
        }
    }

    fun clearError() {
        _error.value = ""
    }
}

// Data class para la sesión con la nueva estructura
data class Sesion(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val master: String = "",
    val jugadores: List<Any>? = emptyList(), // Ahora es una lista de emails (Strings)
    val horarios: List<String>? = emptyList(),
    val notificaciones: List<String>? = emptyList(),
    val fechaCreacion: com.google.firebase.Timestamp? = null,
    val ultimaModificacion: com.google.firebase.Timestamp? = null
)

data class Usuario(
    val email: String = "",
    val nombre: String = ""
)

// Data class para el estado de la sesión
data class SesionState(
    val id: String,
    val nombre: String,
    val descripcion: String?,
    val masterEmail: String,
    val masterNombre: String?,
    val jugadores: List<String>, // Solo emails de jugadores
    val horarios: List<String>,
    val notificaciones: List<String>,
    val fechaCreacion: com.google.firebase.Timestamp?
)

