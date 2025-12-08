package com.example.login.presentation.spellinfo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SpellInfoScreen(
    name: String,
    level: String,
    school: String,
    classes: String,
    description: String,
    onBack: () -> Unit){

    Scaffold(){
        Column {
            Text(
                text = "Nombre: $name",
            )
            Spacer(modifier = Modifier.height(55.dp))
            Text(
                text = "Nivel: $level",
            )
            Spacer(modifier = Modifier.height(55.dp))
            Text(
                text = "Escuela: $school",
            )
            Spacer(modifier = Modifier.height(55.dp))
            Text(
                text = "Clases: $classes",
            )
            Spacer(modifier = Modifier.height(55.dp))
            Text(
                text = "Descripción: $description",
            )
            Spacer(modifier = Modifier.height(80.dp))
            Button(onClick = { onBack() }) {
                Text(text = "Volver")
            }
        }


    }

}