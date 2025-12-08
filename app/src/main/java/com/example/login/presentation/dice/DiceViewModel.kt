package com.example.login.presentation.dice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DiceViewModel : ViewModel() {
    val email = MutableStateFlow("")
    fun setEmail(newEmail: String) {
        email.value = newEmail
    }
    // Estado para el valor actual del dado (sin modificador)
    private val _diceValue = MutableStateFlow(0)
    val diceValue: StateFlow<Int> = _diceValue

    // Estado para saber si está rodando el dado
    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling

    // Historial de tiradas (almacena tanto el valor del dado como el modificador usado)
    private val _rollHistory = MutableStateFlow<List<DiceRoll>>(emptyList())
    val rollHistory: StateFlow<List<DiceRoll>> = _rollHistory

    // Función para rodar el dado con un modificador específico
    fun rollDice(modifier: Int = 0) {
        if (_isRolling.value) return

        _isRolling.value = true
        viewModelScope.launch {
            // Simular tiempo de rodado
            repeat(10) {
                _diceValue.value = (1..20).random()
                delay(100L)
            }

            // Resultado final
            val finalValue = (1..20).random()
            _diceValue.value = finalValue

            // Añadir al historial con el modificador usado en ese momento
            val diceRoll = DiceRoll(
                diceValue = finalValue,
                modifier = modifier
            )

            _rollHistory.value = _rollHistory.value + diceRoll

            _isRolling.value = false
        }
    }

    // Función para limpiar el historial
    fun clearHistory() {
        _rollHistory.value = emptyList()
        _diceValue.value = 0
    }
}