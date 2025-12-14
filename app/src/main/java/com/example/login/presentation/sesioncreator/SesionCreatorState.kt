package com.example.login.presentation.sesioncreator

import com.google.firebase.Timestamp

data class Sesion(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val master: String = "",
    val jugadores: List<String>? = emptyList(),
    val horarios: Map<String, Any>? = null,
    val notificaciones: List<String>? = emptyList(),
    val fechaCreacion: Timestamp? = null,
    val ultimaModificacion: Timestamp? = null
)

data class Usuario(
    val email: String = "",
    val nombre: String = ""
)

data class SesionState(
    val id: String,
    val nombre: String,
    val descripcion: String?,
    val masterEmail: String,
    val masterNombre: String?,
    val jugadores: List<String>,
    val horarioActual: String,
    val listaHorarios: List<String>,
    val listaAceptaciones: Map<String, List<String>>,
    val usuarioHaAceptado: Set<String>,
    val notificaciones: List<String>,
    val fechaCreacion: Timestamp?
)