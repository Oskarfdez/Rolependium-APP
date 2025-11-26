package com.example.login.admin.newscreator

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class NewsCreatorViewModel : ViewModel() {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun subirNoticia(
        context: Context,
        titulo: String,
        subtitulo: String,
        imagenUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (imagenUri == null) {
            onFailure(Exception("No se ha seleccionado imagen"))
            return
        }

        val nombreArchivo = UUID.randomUUID().toString()
        val ruta = "noticias/$nombreArchivo.jpg"
        val imageRef = storage.reference.child(ruta)

        imageRef.putFile(imagenUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val noticia = mapOf(
                        "titulo" to titulo,
                        "subtitulo" to subtitulo,
                        "imagen" to downloadUrl.toString()
                    )
                    firestore.collection("noticias")
                        .add(noticia)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                }.addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }
}
