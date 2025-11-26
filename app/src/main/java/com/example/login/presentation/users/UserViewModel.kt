package com.example.login.presentation.users
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _uploadStatus = MutableLiveData<Result<String>>()
    val uploadStatus: LiveData<Result<String>> = _uploadStatus

    fun subirImagen(email: String, imageUri: Uri) {

        firestore.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDoc = querySnapshot.documents[0]
                    val userId = userDoc.id

                    val imageRef = storageRef.child("usuarios/$userId/imagen.jpg")


                    imageRef.putFile(imageUri)
                        .addOnSuccessListener {
                            imageRef.downloadUrl
                                .addOnSuccessListener { downloadUri ->
                                    firestore.collection("usuarios").document(userId)
                                        .set(mapOf("imagenUrl" to downloadUri.toString()), SetOptions.merge())
                                        .addOnSuccessListener {
                                            _uploadStatus.value = Result.success("Imagen subida y URL guardada")
                                        }
                                        .addOnFailureListener { e ->
                                            _uploadStatus.value = Result.failure(e)
                                        }
                                }
                                .addOnFailureListener { e -> Log.e("Firebase", "Error subida: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            _uploadStatus.value = Result.failure(e)
                        }
                } else {
                    _uploadStatus.value = Result.failure(Exception("No se encontró usuario con email: $email"))
                }
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = Result.failure(e)
            }
    }


    private val _nombre = MutableLiveData<String?>()
    val nombre: LiveData<String?> = _nombre

    private val _telefono = MutableLiveData<String?>()
    val telefono: LiveData<String?> = _telefono

    fun getNombre(email: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    _nombre.value = querySnapshot.documents[0].getString("name")
                } else {
                    _nombre.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _nombre.value = null
            }
        }
    }

    fun getTelefono(email: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    _telefono.value = querySnapshot.documents[0].getString("numero de telefono")
                } else {
                    _telefono.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _telefono.value = null
            }
        }
    }
}
