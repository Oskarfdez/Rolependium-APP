package com.example.login.presentation.sesioncreator
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class SesionCreatorViewModel: ViewModel() {
    private val _email = MutableStateFlow("")
    private val db = FirebaseFirestore.getInstance()

    // Estados observables
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun setEmail(email: String) {
        _email.value = email
    }

    /**
     * Crea una nueva sesión en Firestore
     */
    fun setSesion(nombre: String, descripcion: String) {
        if (_email.value.isEmpty()) {
            _error.value = "Error: No se ha configurado el email del usuario"
            return
        }

        if (nombre.isBlank() || descripcion.isBlank()) {
            _error.value = "Error: Nombre y descripción son obligatorios"
            return
        }

        _isLoading.value = true
        _success.value = false
        _error.value = ""

        viewModelScope.launch {
            try {
                // Crear un ID único para la sesión
                val sesionId = UUID.randomUUID().toString()

                // Crear el objeto de sesión con estructura para múltiples jugadores
                val sesion = hashMapOf(
                    "id" to sesionId,
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "master" to _email.value,
                    "jugadores" to listOf(_email.value), // El master también está en la lista
                    "horarios" to mapOf(
                        "horario_actual" to "", // String vacío inicialmente
                        "lista_horarios" to emptyList<String>() // Lista de horarios disponibles
                        // Las aceptaciones se crearán dinámicamente por cada horario
                    ),
                    "notificaciones" to emptyList<String>(),
                    "fechaCreacion" to com.google.firebase.Timestamp.now(),
                    "ultimaModificacion" to com.google.firebase.Timestamp.now()
                )

                // Guardar la sesión en Firestore
                db.collection("sesiones")
                    .document(sesionId)
                    .set(sesion)
                    .await()

                _success.value = true

            } catch (e: Exception) {
                _error.value = "Error al crear la sesión: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }

        }
    }

    /**
     * Resetea los estados de error y éxito
     */
    fun resetStates() {
        _success.value = false
        _error.value = ""
    }
}