package com.example.login.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataViewModel: ViewModel() {
    private val _newsList = MutableLiveData<List<NewsData>>()
    val newsList: LiveData<List<NewsData>> = _newsList

    fun listarNoticias() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("noticias")
                    .get()
                    .await()

                val newsItems = snapshot.documents.mapNotNull { doc ->
                    val titulo = doc.getString("titulo")
                    val subtitulo = doc.getString("subtitulo")
                    val imagenUrl = doc.getString("imagen")

                    if (titulo != null && subtitulo != null && imagenUrl != null) {
                        NewsData(imagen = imagenUrl, titulo = titulo, subtitulo = subtitulo)
                    } else null
                }

                _newsList.value = newsItems

            } catch (e: Exception) {
                e.printStackTrace()
                _newsList.value = emptyList()
            }
        }
    }
}