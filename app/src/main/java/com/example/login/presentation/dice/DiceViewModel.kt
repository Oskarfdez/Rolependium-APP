package com.example.login.presentation.dice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class DiceViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    fun setEmail(email: String) {
        _email.value = email
    }

    private val _diceValue = MutableStateFlow(1)
    val diceValue: StateFlow<Int> = _diceValue.asStateFlow()

    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

    private val _rollHistory = MutableStateFlow<List<Int>>(emptyList())
    val rollHistory: StateFlow<List<Int>> = _rollHistory.asStateFlow()

    // Variable para controlar el cooldown
    private var canRoll = true

    fun rollDice() {
        // Verificar si puede rodar (no está en cooldown y no está rodando)
        if (!canRoll || _isRolling.value) {
            return
        }

        viewModelScope.launch {
            // Activar cooldown
            canRoll = false
            _isRolling.value = true

            // Simular animación de rodado (5 cambios rápidos)
            repeat(5) {
                _diceValue.value = Random.nextInt(1, 21)
                delay(80L)
            }

            // Valor final
            val finalValue = Random.nextInt(1, 21)
            _diceValue.value = finalValue

            // Agregar al historial
            _rollHistory.value = _rollHistory.value + finalValue

            _isRolling.value = false

            // Esperar 2 segundos antes de permitir otra tirada
            delay(2000L)
            canRoll = true
        }
    }

    fun clearHistory() {
        _rollHistory.value = emptyList()
    }

    fun onShakeDetected() {
        rollDice()
    }

    fun canRoll(): Boolean {
        return canRoll && !_isRolling.value
    }
}