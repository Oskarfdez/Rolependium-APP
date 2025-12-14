package com.example.login.presentation.sesiondata


import com.google.firebase.Timestamp

// Data class para representar la sesión en Firestore
data class Sesion(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val master: String = "",
    val jugadores: List<String>? = emptyList(), // Lista de emails de todos los participantes
    val horarios: Map<String, Any>? = null,
    val notificaciones: List<String>? = emptyList(),
    val fechaCreacion: Timestamp? = null,
    val ultimaModificacion: Timestamp? = null
)

// Data class para la sesión con información completa
data class SesionData(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val masterEmail: String,
    val masterNombre: String,
    val jugadores: List<String>, // Todos los participantes (incluye master)
    val jugadoresNoMaster: List<String>, // Solo jugadores (no master)
    val horarioActual: String,
    val listaHorarios: List<String>,
    val listaAceptaciones: Map<String, List<String>>,
    val usuarioHaAceptado: Set<String>,
    val notificaciones: List<String>,
    val fechaCreacion: Timestamp?
)

data class JugadorConNombre(
    val email: String,
    val nombre: String,
    val esMaster: Boolean
)

// Data class para el usuario
data class Usuario(
    val email: String = "",
    val name: String = ""
)
