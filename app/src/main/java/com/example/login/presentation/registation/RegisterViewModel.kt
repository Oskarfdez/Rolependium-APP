package com.example.login.presentation.registation


import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {
    val state: MutableState<RegisterState> = mutableStateOf(RegisterState())
     private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(
        name: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
    ) {
        val errorMessage = when {
            name.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank() || confirmPassword.isBlank() -> R.string.error_input_empty
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_not_a_valid_email
            !Patterns.PHONE.matcher(phoneNumber).matches() -> R.string.error_not_a_valid_phone_number
            password != confirmPassword -> R.string.error_incorrectly_repeated_password
            else -> null
        }

        errorMessage?.let {
            state.value = state.value.copy(errorMessage = it)
            return
        }

        state.value = state.value.copy(displayProgressBar = true)

        val db = FirebaseFirestore.getInstance()

        val characterData = hashMapOf(
            "name" to name,
            "email" to email,
            "numero de telefono" to phoneNumber,
        )

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    state.value = state.value.copy(displayProgressBar = false)
                    if (task.isSuccessful) {
                        state.value = state.value.copy(successRegister = true)
                    } else {
                        state.value = state.value.copy(errorMessage = R.string.error_registration_failed)
                    }
                }
            db.collection("usuarios")
                .add(characterData)
                .addOnSuccessListener { "Usuario Creado correctamente" }
                .addOnFailureListener { "Error al crear el usuario" }
        }




    }

    fun hideErrorDialog() {
        state.value = state.value.copy(errorMessage = null)
    }
}