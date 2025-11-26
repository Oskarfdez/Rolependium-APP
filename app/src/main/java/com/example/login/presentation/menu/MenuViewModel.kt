package com.example.login.presentation.menu

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MenuViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    fun setEmail(newEmail: String) {
        if (_email.value.isEmpty()) {
            _email.value = newEmail
        }
    }
}