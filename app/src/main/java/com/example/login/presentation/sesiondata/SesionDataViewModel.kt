package com.example.login.presentation.sesiondata
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SesionDataViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    private val db = FirebaseFirestore.getInstance()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _sesionData = MutableStateFlow<SesionData?>(null)
    val sesionData: StateFlow<SesionData?> = _sesionData

    private val _showLeaveSessionDialog = MutableStateFlow(false)
    val showLeaveSessionDialog: StateFlow<Boolean> = _showLeaveSessionDialog

    private val _showDeleteSessionDialog = MutableStateFlow(false)
    val showDeleteSessionDialog: StateFlow<Boolean> = _showDeleteSessionDialog

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _showAddHorarioDialog = MutableStateFlow(false)
    val showAddHorarioDialog: StateFlow<Boolean> = _showAddHorarioDialog

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _horariosSeleccionados = MutableStateFlow<MutableSet<String>>(mutableSetOf())
    val horariosSeleccionados: StateFlow<Set<String>> = _horariosSeleccionados

    private val _showConfirmDialog = MutableStateFlow(false)
    val showConfirmDialog: StateFlow<Boolean> = _showConfirmDialog

    fun setEmail(email: String) {
        _email.value = email
    }

    fun showAddHorarioDialog() {
        _showAddHorarioDialog.value = true
    }

    fun hideAddHorarioDialog() {
        _showAddHorarioDialog.value = false
    }

    fun showConfirmDialog() {
        _showConfirmDialog.value = true
    }

    fun hideConfirmDialog() {
        _showConfirmDialog.value = false
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        // Limpiar el mensaje después de un tiempo
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _toastMessage.value = null
        }
    }

    fun addHorario(sesionId: String, nuevoHorario: String) {
        if (nuevoHorario.isBlank()) {
            _error.value = "Error: El horario no puede estar vacío"
            return
        }

        viewModelScope.launch {
            try {
                db.collection("sesiones")
                    .document(sesionId)
                    .update(
                        "horarios.lista_horarios", FieldValue.arrayUnion(nuevoHorario),
                        "ultimaModificacion", com.google.firebase.Timestamp.now()
                    )
                    .await()

                _sesionData.value?.let { sesionActual ->
                    val listaHorariosActualizada = sesionActual.listaHorarios.toMutableList()
                    listaHorariosActualizada.add(nuevoHorario)

                    _sesionData.value = sesionActual.copy(
                        listaHorarios = listaHorariosActualizada
                    )
                }

                showToast("Horario añadido correctamente")
                hideAddHorarioDialog()

            } catch (e: Exception) {
                _error.value = "Error al añadir el horario: ${e.message}"
                e.printStackTrace()
            }
        }
    }


    fun confirmarHorariosSeleccionados(sesionId: String) {
        val horarios = _horariosSeleccionados.value
        if (horarios.isEmpty()) {
            _error.value = "Error: No has seleccionado ningún horario"
            return
        }

        viewModelScope.launch {
            try {
                for (horario in horarios) {
                    val aceptacionesPath = "horarios.aceptados_${horario}"

                    db.collection("sesiones")
                        .document(sesionId)
                        .update(
                            aceptacionesPath, FieldValue.arrayUnion(_email.value),
                            "ultimaModificacion", com.google.firebase.Timestamp.now()
                        )
                        .await()
                }

                _sesionData.value?.let { sesionActual ->
                    val nuevosHorariosAceptados = sesionActual.usuarioHaAceptado.toMutableSet()
                    nuevosHorariosAceptados.addAll(horarios)

                    val nuevaListaAceptaciones = sesionActual.listaAceptaciones.toMutableMap()

                    for (horario in horarios) {
                        val aceptacionesActuales = nuevaListaAceptaciones[horario] ?: emptyList()
                        val nuevasAceptaciones = aceptacionesActuales + _email.value
                        nuevaListaAceptaciones[horario] = nuevasAceptaciones
                    }

                    _sesionData.value = sesionActual.copy(
                        usuarioHaAceptado = nuevosHorariosAceptados,
                        listaAceptaciones = nuevaListaAceptaciones
                    )
                }

                _horariosSeleccionados.value = mutableSetOf()

                loadSesionData(sesionId)

                showToast("Horarios confirmados correctamente")
                hideConfirmDialog()

            } catch (e: Exception) {
                _error.value = "Error al confirmar horarios: ${e.message}"
                e.printStackTrace()
            }
        }
    }




    fun toggleHorarioSeleccionado(horario: String) {
        val current = _horariosSeleccionados.value.toMutableSet()
        if (current.contains(horario)) {
            current.remove(horario)
        } else {
            current.add(horario)
        }
        _horariosSeleccionados.value = current
    }


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
        _horariosSeleccionados.value = mutableSetOf()

        viewModelScope.launch {
            try {
                val sesionDoc = db.collection("sesiones")
                    .document(sesionId)
                    .get()
                    .await()

                if (!sesionDoc.exists()) {
                    _error.value = "Error: Sesión no encontrada"
                    return@launch
                }

                val sesion = sesionDoc.toObject(Sesion::class.java) ?: run {
                    _error.value = "Error: Formato de sesión inválido"
                    return@launch
                }

                val jugadoresConNombres = mutableListOf<JugadorConNombre>()

                for (email in sesion.jugadores ?: emptyList()) {
                    val nombre = getUserName(email)
                    jugadoresConNombres.add(
                        JugadorConNombre(
                            email = email,
                            nombre = nombre ?: email,
                            esMaster = email == sesion.master
                        )
                    )
                }

                val horarioActual = sesion.horarios?.get("horario_actual") as? String ?: ""
                val listaHorarios = sesion.horarios?.get("lista_horarios") as? List<String> ?: emptyList()

                val listaAceptacionesMap = mutableMapOf<String, List<String>>()

                sesion.horarios?.forEach { (key, value) ->
                    if (key.startsWith("aceptados_")) {
                        val horarioKey = key.removePrefix("aceptados_")
                        val aceptaciones = value as? List<String> ?: emptyList()
                        listaAceptacionesMap[horarioKey] = aceptaciones
                    }
                }

                val usuarioHaAceptado = mutableSetOf<String>()
                listaAceptacionesMap.forEach { (horario, aceptaciones) ->
                    if (_email.value in aceptaciones) {
                        usuarioHaAceptado.add(horario)
                    }
                }

                val jugadoresNoMaster = (sesion.jugadores ?: emptyList())
                    .filter { it != sesion.master }

                val sesionData = SesionData(
                    id = sesionDoc.id,
                    nombre = sesion.nombre,
                    descripcion = sesion.descripcion,
                    masterEmail = sesion.master,
                    masterNombre = getUserName(sesion.master) ?: sesion.master,
                    jugadores = sesion.jugadores ?: emptyList(),
                    jugadoresNoMaster = jugadoresNoMaster, // Solo jugadores, no master
                    horarioActual = horarioActual,
                    listaHorarios = listaHorarios,
                    listaAceptaciones = listaAceptacionesMap,
                    usuarioHaAceptado = usuarioHaAceptado,
                    notificaciones = sesion.notificaciones ?: emptyList(),
                    fechaCreacion = sesion.fechaCreacion
                )

                _sesionData.value = sesionData

                val seleccionesUsuario = usuarioHaAceptado.toMutableSet()
                _horariosSeleccionados.value = seleccionesUsuario

            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getUserName(email: String): String? {
        return try {
            val doc = db.collection("usuarios")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (!doc.isEmpty) {
                val usuario = doc.documents[0].toObject(Usuario::class.java)
                usuario?.name ?: email
            } else {
                email
            }
        } catch (e: Exception) {
            e.printStackTrace()
            email
        }
    }
    fun eliminarJugador(sesionId: String, emailJugador: String) {
        if (sesionId.isEmpty() || emailJugador.isEmpty()) {
            _error.value = "Error: Datos inválidos"
            return
        }

        viewModelScope.launch {
            try {
                db.collection("sesiones")
                    .document(sesionId)
                    .update(
                        "jugadores", FieldValue.arrayRemove(emailJugador),
                        "ultimaModificacion", com.google.firebase.Timestamp.now()
                    )
                    .await()

                val sesionDoc = db.collection("sesiones")
                    .document(sesionId)
                    .get()
                    .await()

                if (!sesionDoc.exists()) {
                    _error.value = "Error: Sesión no encontrada"
                    return@launch
                }

                val sesion = sesionDoc.toObject(Sesion::class.java)
                if (sesion == null) {
                    _error.value = "Error: Formato de sesión inválido"
                    return@launch
                }

                sesion.horarios?.forEach { (key, value) ->
                    if (key.startsWith("aceptados_")) {
                        val aceptaciones = value as? List<String> ?: emptyList()
                        if (emailJugador in aceptaciones) {
                            val nuevasAceptaciones = aceptaciones.filter { it != emailJugador }
                            db.collection("sesiones")
                                .document(sesionId)
                                .update(
                                    "horarios.$key", nuevasAceptaciones
                                )
                                .await()
                        }
                    }
                }

                loadSesionData(sesionId)

                showToast("Jugador eliminado correctamente")

            } catch (e: Exception) {
                _error.value = "Error al eliminar jugador: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _error.value = ""
    }

    //ESTA SERIE DE FUNCIONES SE ENCUENTRAN FUERA DE PLAZO SOLO LAS AÑADÍ POR QUE QUIERO SEGUIR AVANZANDO EN ESTE PROYECTO PERSONALMENTE
    //(Su funcion es eliminar sesiones y/o jugadores de la base de datos)
    fun showLeaveSessionDialog() {
        _showLeaveSessionDialog.value = true
    }

    fun hideLeaveSessionDialog() {
        _showLeaveSessionDialog.value = false
    }

    fun showDeleteSessionDialog() {
        _showDeleteSessionDialog.value = true
    }

    fun hideDeleteSessionDialog() {
        _showDeleteSessionDialog.value = false
    }

    fun leaveSession(sesionId: String, email: String) {
        if (sesionId.isEmpty() || email.isEmpty()) {
            _error.value = "Error: Invalid data"
            return
        }

        viewModelScope.launch {
            try {
                db.collection("sesiones")
                    .document(sesionId)
                    .update(
                        "jugadores", FieldValue.arrayRemove(email),
                        "ultimaModificacion", com.google.firebase.Timestamp.now()
                    )
                    .await()

                val sesionDoc = db.collection("sesiones")
                    .document(sesionId)
                    .get()
                    .await()

                if (!sesionDoc.exists()) {
                    _error.value = "Error: Session not found"
                    return@launch
                }

                val sesion = sesionDoc.toObject(Sesion::class.java)
                sesion?.horarios?.forEach { (key, value) ->
                    if (key.startsWith("aceptados_")) {
                        val aceptaciones = value as? List<String> ?: emptyList()
                        if (email in aceptaciones) {
                            val nuevasAceptaciones = aceptaciones.filter { it != email }
                            db.collection("sesiones")
                                .document(sesionId)
                                .update(
                                    "horarios.$key", nuevasAceptaciones
                                )
                                .await()
                        }
                    }
                }

                showToast("You have left the session successfully")
                hideLeaveSessionDialog()

            } catch (e: Exception) {
                _error.value = "Error leaving session: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun deleteSession(sesionId: String) {
        if (sesionId.isEmpty()) {
            _error.value = "Error: Invalid session ID"
            return
        }

        viewModelScope.launch {
            try {
                db.collection("sesiones")
                    .document(sesionId)
                    .delete()
                    .await()

                showToast("Session deleted successfully")
                hideDeleteSessionDialog()

            } catch (e: Exception) {
                _error.value = "Error deleting session: ${e.message}"
                e.printStackTrace()
            }
        }
    }

}

