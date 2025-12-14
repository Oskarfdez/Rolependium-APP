package com.example.login.presentation.sesion
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.presentation.sesioncreator.Sesion
import com.example.login.presentation.sesioncreator.SesionState
import com.example.login.presentation.sesioncreator.Usuario
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

    private val _showJoinDialog = MutableStateFlow(false)
    val showJoinDialog: StateFlow<Boolean> = _showJoinDialog

    private val _joinDialogMessage = MutableStateFlow("")
    val joinDialogMessage: StateFlow<String> = _joinDialogMessage


    fun setEmail(email: String) {
        _email.value = email
    }

    fun showJoinDialog() {
        _showJoinDialog.value = true
        _joinDialogMessage.value = ""
    }

    fun hideJoinDialog() {
        _showJoinDialog.value = false
        _joinDialogMessage.value = ""
    }

    fun joinSesion(sesionId: String, context: Context) {
        if (_email.value.isEmpty()) {
            _error.value = "Error: No se ha configurado el email del usuario"
            return
        }

        if (sesionId.isEmpty()) {
            _joinDialogMessage.value = "Error: El ID de sesión no puede estar vacío"
            return
        }

        viewModelScope.launch {
            try {
                //Accede a datos
                val sesionDoc = db.collection("sesiones")
                    .document(sesionId)
                    .get()
                    .await()
                if (!sesionDoc.exists()) {
                    _joinDialogMessage.value = "Error: No se encontró ninguna sesión con ese ID"
                    return@launch
                }
                val sesion = sesionDoc.toObject(Sesion::class.java)
                if (sesion == null) {
                    _joinDialogMessage.value = "Error: Formato de sesión inválido"
                    return@launch
                }

                //Comprueba si eres master
                if (sesion.master == _email.value) {
                    _joinDialogMessage.value = "Error: Ya eres el master de esta sesión"
                    return@launch
                }

                //Comprueba si ya estás en la sesión
                val jugadoresActuales = sesion.jugadores ?: emptyList()
                if (jugadoresActuales.contains(_email.value)) {
                    _joinDialogMessage.value = "Error: Ya estás en esta sesión"
                    return@launch
                }

                db.collection("sesiones")
                    .document(sesionId)
                    .update(
                        "jugadores", FieldValue.arrayUnion(_email.value),
                        "ultimaModificacion", com.google.firebase.Timestamp.now()
                    )
                    .await()

                _joinDialogMessage.value = "¡Te has unido a la sesión '${sesion.nombre}' exitosamente!"

                //Recargar la lista de sesiones
                kotlinx.coroutines.delay(1000)
                loadSesiones(context)

                //Ocultar el diálogo
                kotlinx.coroutines.delay(2000)
                hideJoinDialog()

            } catch (e: Exception) {
                _joinDialogMessage.value = "Error al unirse a la sesión: ${e.message}"
                e.printStackTrace()
            }
        }
    }


    fun loadSesiones(context: Context) {

        // error que ocurre si el valor email está vacío (Esto solo me ocurría cuando añadia los datos de la cuenta manualmente)
        if (_email.value.isEmpty()) {
            _error.value = "Error: No se ha configurado el email del usuario"
            return
        }
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            try {
                val emailUsuario = _email.value

                // Buscar sesiones donde el usuario esta en la lista de jugadores
                val sesionesDelUsuario = db.collection("sesiones")
                    .whereArrayContains("jugadores", emailUsuario)
                    .get()
                    .await()

                // Convertir a SesionState y obtener nombres
                val sesionesConNombres = mutableListOf<SesionState>()

                for (doc in sesionesDelUsuario.documents) {
                    val sesion = doc.toObject(Sesion::class.java) ?: continue

                    val masterNombre = if (sesion.master == emailUsuario) {
                        "You"
                    } else {
                        getUserName(sesion.master)
                    }

                    // Obtener datos de horarios
                    val horarioActual = sesion.horarios?.get("horario_actual") as? String ?: ""
                    val listaHorarios = sesion.horarios?.get("lista_horarios") as? List<String> ?: emptyList()

                    // Obtener lista de aceptaciones por horario
                    val listaAceptacionesMap = mutableMapOf<String, List<String>>()

                    // Extraer aceptaciones
                    sesion.horarios?.forEach { (key, value) ->
                        if (key.startsWith("aceptados_")) {
                            val horarioKey = key.removePrefix("aceptados_")
                            val aceptaciones = value as? List<String> ?: emptyList()
                            listaAceptacionesMap[horarioKey] = aceptaciones
                        }
                    }

                    // Verificar si el usuario actual ya ha aceptado algún horario
                    val usuarioHaAceptado = mutableSetOf<String>()
                    listaAceptacionesMap.forEach { (horario, aceptaciones) ->
                        if (emailUsuario in aceptaciones) {
                            usuarioHaAceptado.add(horario)
                        }
                    }

                    val sesionState = SesionState(
                        id = doc.id,
                        nombre = sesion.nombre,
                        descripcion = sesion.descripcion,
                        masterEmail = sesion.master,
                        masterNombre = masterNombre,
                        jugadores = sesion.jugadores ?: emptyList(),
                        horarioActual = horarioActual,
                        listaHorarios = listaHorarios,
                        listaAceptaciones = listaAceptacionesMap,
                        usuarioHaAceptado = usuarioHaAceptado,
                        notificaciones = sesion.notificaciones ?: emptyList(),
                        fechaCreacion = sesion.fechaCreacion
                    )

                    sesionesConNombres.add(sesionState)
                }

                // Ordenar por fecha de creación
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
