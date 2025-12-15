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
    private val _diceValue = MutableStateFlow(0)
    val diceValue: StateFlow<Int> = _diceValue

    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling

    private val _rollHistory = MutableStateFlow<List<DiceRoll>>(emptyList())
    val rollHistory: StateFlow<List<DiceRoll>> = _rollHistory

    fun rollDice(modifier: Int = 0) {
        if (_isRolling.value) return

        _isRolling.value = true
        viewModelScope.launch {
            repeat(10) {
                _diceValue.value = (1..20).random()
                delay(100L)
            }

            val finalValue = (1..20).random()
            _diceValue.value = finalValue

            val diceRoll = DiceRoll(
                diceValue = finalValue,
                modifier = modifier
            )

            _rollHistory.value = _rollHistory.value + diceRoll

            _isRolling.value = false
        }
    }

    fun clearHistory() {
        _rollHistory.value = emptyList()
        _diceValue.value = 0
    }
}