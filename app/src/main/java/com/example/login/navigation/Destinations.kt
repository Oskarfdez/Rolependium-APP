package com.example.login.navigation


import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Destinations(
    val route: String,
    val arguments: List<NamedNavArgument>
){

    object Login: Destinations("login", emptyList())
    object Register: Destinations("register", emptyList())
    object Menu: Destinations("menu",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Creator: Destinations("character",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Character: Destinations("register",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object User: Destinations("user",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Dice: Destinations("dice",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Admin: Destinations("admin", emptyList())
    object NewsCreator: Destinations("news_creator", emptyList())


}