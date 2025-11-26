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

    fun setEmail(newEmail: String) {
        if (_email.value.isEmpty()) {
            _email.value = newEmail
        }
    }

    private val _diceValue = MutableStateFlow(1)
    val diceValue: StateFlow<Int> = _diceValue.asStateFlow()

    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

    private val _rollHistory = MutableStateFlow<List<Int>>(emptyList())
    val rollHistory: StateFlow<List<Int>> = _rollHistory.asStateFlow()

    // Variable para controlar si puede iniciar una nueva tirada
    private var canStartRoll = true

    fun rollDice() {
        // Verificar si puede iniciar una nueva tirada
        if (!canStartRoll) {
            return
        }

        viewModelScope.launch {
            // Bloquear nuevas tiradas
            canStartRoll = false
            _isRolling.value = true

            // Esperar 2 segundos antes de mostrar el resultado
            delay(2000L)

            // Generar y mostrar el valor final
            val finalValue = Random.nextInt(1, 21)
            _diceValue.value = finalValue

            // Agregar al historial
            _rollHistory.value = _rollHistory.value + finalValue

            // Finalizar el estado de rodado
            _isRolling.value = false

            // Permitir nuevas tiradas nuevamente
            canStartRoll = true
        }
    }

    fun clearHistory() {
        _rollHistory.value = emptyList()
    }

    // Función para detectar agitación
    fun onShakeDetected() {
        rollDice()
    }
}