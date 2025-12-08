package com.example.login.presentation.sesiondata


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SesionDataViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    private val db = FirebaseFirestore.getInstance()

    // Estados observables
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _sesionData = MutableStateFlow<SesionData?>(null)
    val sesionData: StateFlow<SesionData?> = _sesionData

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _horariosVotacion = MutableStateFlow<HorariosVotacion?>(null)
    val horariosVotacion: StateFlow<HorariosVotacion?> = _horariosVotacion

    /**
     * Establece el email del usuario actual
     */
    fun setEmail(email: String) {
        _email.value = email
    }

    /**
     * Carga los datos de una sesión específica por ID
     */
    fun loadSesionData(sesionId: String) {
        if (_email.value.isEmpty()) {
            _error.value = "Error: No se ha configurado el email del usuario"
            return
        }

        if (sesionId.isEmpty()) {
            _error.value = "Error: ID de sesión no válido"
            return
        }

        _isLoading.value = true
        _error.value = ""
        _sesionData.value = null
        _horariosVotacion.value = null

        viewModelScope.launch {
            try {
                // 1. Obtener datos básicos de la sesión
                val sesionDoc = db.collection("sesiones")
                    .document(sesionId)
                    .get()
                    .await()

                if (!sesionDoc.exists()) {
                    _error.value = "Error: Sesión no encontrada"
                    return@launch
                }

                // Convertir documento a SesionData
                val sesion = sesionDoc.toObject(Sesion::class.java) ?: run {
                    _error.value = "Error: Formato de sesión inválido"
                    return@launch
                }

                // Obtener el ID real del documento
                val idReal = sesionDoc.id

                // Obtener nombres de los jugadores
                val jugadoresConNombres = mutableListOf<JugadorConNombre>()

                // Obtener nombre del master
                val masterNombre = getUserName(sesion.master)
                jugadoresConNombres.add(
                    JugadorConNombre(
                        email = sesion.master,
                        nombre = masterNombre ?: sesion.master,
                        esMaster = true
                    )
                )

                // Obtener nombres de los demás jugadores
                // Los jugadores ahora son una lista de Strings (emails)
                val jugadoresEmails = sesion.jugadores?.mapNotNull {
                    it as? String
                } ?: emptyList()

                for (jugadorEmail in jugadoresEmails) {
                    // Saltar el master que ya añadimos
                    if (jugadorEmail != sesion.master) {
                        val jugadorNombre = getUserName(jugadorEmail)
                        jugadoresConNombres.add(
                            JugadorConNombre(
                                email = jugadorEmail,
                                nombre = jugadorNombre ?: jugadorEmail,
                                esMaster = false
                            )
                        )
                    }
                }

                // Crear el objeto SesionData con la información completa
                val sesionData = SesionData(
                    id = idReal,
                    nombre = sesion.nombre,
                    descripcion = sesion.descripcion,
                    master = sesion.master,
                    jugadores = jugadoresEmails,
                    horarios = sesion.horarios ?: emptyList(),
                    notificaciones = sesion.notificaciones ?: emptyList(),
                    fechaCreacion = sesion.fechaCreacion,
                    ultimaModificacion = sesion.ultimaModificacion,
                    jugadoresConNombres = jugadoresConNombres
                )

                _sesionData.value = sesionData

                // 2. Obtener datos de horarios de votación
                try {
                    val horariosDoc = db.collection("sesiones")
                        .document(sesionId)
                        .collection("horarios")
                        .document("votaciones")
                        .get()
                        .await()

                    if (horariosDoc.exists()) {
                        val votacion = horariosDoc.toObject(HorariosVotacion::class.java)
                        _horariosVotacion.value = votacion
                    }
                } catch (e: Exception) {
                    // Si no existe la subcolección, no es un error crítico
                    println("No se encontró la subcolección de horarios: ${e.message}")
                }

            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene el nombre de usuario desde Firestore
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
                usuario?.nombre
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resetea el estado de error
     */
    fun clearError() {
        _error.value = ""
    }
}

// Data classes (deben estar en el mismo archivo o en archivos separados)

// Data class para representar la sesión en Firestore (igual que en SesionCreatorViewModel)
data class Sesion(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val master: String = "",
    val jugadores: List<Any>? = emptyList(), // Lista de emails (Strings)
    val horarios: List<String>? = emptyList(),
    val notificaciones: List<String>? = emptyList(),
    val fechaCreacion: com.google.firebase.Timestamp? = null,
    val ultimaModificacion: com.google.firebase.Timestamp? = null
)

// Data class para la sesión con información completa
data class SesionData(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val master: String,
    val jugadores: List<String>,
    val horarios: List<String>,
    val notificaciones: List<String>,
    val fechaCreacion: com.google.firebase.Timestamp?,
    val ultimaModificacion: com.google.firebase.Timestamp?,
    val jugadoresConNombres: List<JugadorConNombre>
)

data class JugadorConNombre(
    val email: String,
    val nombre: String,
    val esMaster: Boolean
)

data class HorariosVotacion(
    val sesion: List<String> = emptyList(),
    val aceptados: Int = 0
)

data class Usuario(
    val email: String = "",
    val nombre: String = ""
)