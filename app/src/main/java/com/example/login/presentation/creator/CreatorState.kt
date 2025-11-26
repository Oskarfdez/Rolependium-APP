package com.example.login.presentation.creator

import androidx.compose.ui.graphics.Color

data class CreatorState(
    val funciona: Boolean = false,
    var compranums: Int = 0,
    var colortext: Color = Color.Green
) {
}