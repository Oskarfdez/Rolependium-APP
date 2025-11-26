package com.example.login.presentation.login

import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val state: MutableState<LoginState> = mutableStateOf(LoginState())
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun login(email: String, password: String) {
        val errorMessage = when {
            email.isBlank() || password.isBlank() -> R.string.error_input_empty
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() && email != "admin" -> R.string.error_not_a_valid_email
            else -> null
        }

        errorMessage?.let {
            state.value = state.value.copy(errorMessage = it)
            return
        }

        if (email == "admin" && password == "admin") {
            state.value = state.value.copy(
                adminLogin = true)
            return
        }

        state.value = state.value.copy(displayProgressBar = true)

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    state.value = state.value.copy(displayProgressBar = false)
                    if (task.isSuccessful) {
                        state.value = state.value.copy(
                            successLogin = true,
                            email = email,
                            password = password
                        )
                    } else {
                        state.value =
                            state.value.copy(errorMessage = R.string.error_invalid_credentials)
                    }
                }
        }
    }
    fun hideErrorDialog() {
        state.value = state.value.copy(errorMessage = null)
    }
}
