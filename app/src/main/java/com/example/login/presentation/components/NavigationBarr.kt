package com.example.login.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.res.painterResource
import com.example.login.R


@Composable
    fun BottomBar(onNavigateToDice: () -> Unit,
                  onNavigateToList: () -> Unit,
                  onNavigateToHome: () -> Unit,
                  onNavigateToSpells: () -> Unit
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,

            ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.sword_24),
                        contentDescription = null
                    )
                },
                label = {
                    Text("Sessions")
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
                    Text("Characters")
                },
                selected = false,
                onClick = {
                    onNavigateToList()
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.scroll_24),
                        contentDescription = null
                    )
                },
                label = {
                    Text("Spells")
                },
                selected = false,
                onClick = {
                    onNavigateToSpells()
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.dice_24),
                        contentDescription = null
                    )
                },
                label = {
                    Text("Dice Roller")
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant),

        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "ROLEPENDIUM")
            }
        },
        actions = {
            IconButton(onClick = { onNavigateToUser()}) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "User")
            }
        }
    )
}

