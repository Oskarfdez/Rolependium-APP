package com.example.login.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.login.ui.theme.ROLERED


@Composable
    fun BottomBar(modifier: androidx.compose.ui.Modifier,
                  onNavigateToDice: () -> Unit,
                  onNavigateToList: () -> Unit,
                  onNavigateToHome: () -> Unit) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,

            ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Menú")
                },
                selected = false,
                onClick = {
                    onNavigateToHome()
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccessibilityNew,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Listado")
                },
                selected = false,
                onClick = {
                    onNavigateToList()
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Creador")
                },
                selected = false,
                onClick = {
                    onNavigateToDice()
                }
            )
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onNavigateToUser: () -> Unit,
    onBack: () -> Unit
) {



    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = ROLERED,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = {onBack()}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "ROLEPENDIUM")
            }
        },
        actions = {
            IconButton(onClick = { onNavigateToUser()}) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Perfil")
            }
        }
    )
}

