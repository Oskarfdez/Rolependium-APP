package com.example.login.presentation.users

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.login.R
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UsuarioScreen(
    email: String,
    onNavigateToHome: () -> Unit,
    onNavigateToSingOut : () -> Unit
) {
    val viewModel: UserViewModel = hiltViewModel()
    val nombre by viewModel.nombre.observeAsState()
    val telefono by viewModel.telefono.observeAsState()

    LaunchedEffect(email) {
        viewModel.getNombre(email)
        viewModel.getTelefono(email)
    }

    Scaffold(
        topBar = { UserTopBar(onNavigateToHome) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(55.dp))
            ImagenSubir(
                email = email,
                viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Nombre: ${nombre ?: "No encontrado"}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Teléfono: ${telefono ?: "No encontrado"}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {onNavigateToSingOut()},) {
                Text("Cerrar sesión")
            }
        }
    }

}

@Composable
fun ImagenSubir(
    email: String,
    viewModel: UserViewModel
) {
    val firestore = FirebaseFirestore.getInstance()

    var storedImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(email) {
        firestore.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    storedImageUrl = doc.getString("imagenUrl")
                }
            }
    }

    val painter = when {
        selectedImageUri != null -> rememberAsyncImagePainter(selectedImageUri)
        storedImageUrl != null -> rememberAsyncImagePainter(storedImageUrl)
        else -> rememberAsyncImagePainter(R.drawable.elf_icon)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = "Imagen de usuario",
            modifier = Modifier
                .size(150.dp)
                .clickable { launcher.launch("image/*") },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedImageUri?.let { uri ->
                    viewModel.subirImagen(email, uri)
                }
            },
            enabled = selectedImageUri != null
        ) {
            Text(text = "Subir imagen")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopBar(
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { onBack()}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "ROLEPENDIUM")
            }
        }
    )
}









