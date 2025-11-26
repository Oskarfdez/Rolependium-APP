package com.example.login.presentation.chardata

data class CharDataState(
    val nombre: String,
    val clase: String,
    val hitpoints: Int,
    val caracteristicas: Map<String, Int>,
    val proficiencia: Int,
    val habilidades: Map<String, Int>,
    val salvaciones: Map<String, Boolean>,
    val armorClass: Int,
    val email: String,
    val notas: String
)
