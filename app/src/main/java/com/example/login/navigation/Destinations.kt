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
    object Sesion: Destinations("sesion",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Creator: Destinations("creator",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Character: Destinations("character",
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
    object Spells: Destinations("spells",
        listOf(
            navArgument("email"){ type = NavType.StringType },
        ))
    object Admin: Destinations("admin", emptyList())

    object NewsCreator: Destinations("news_creator", emptyList())
    object SpellInfo: Destinations("spell_info",
        listOf(
            navArgument("name"){ type = NavType.StringType },
            navArgument("level"){ type = NavType.StringType },
            navArgument("school"){ type = NavType.StringType },
            navArgument("classes"){ type = NavType.StringType },
            navArgument("description"){ type = NavType.StringType }
        ))
    object SesionCreator: Destinations("sesion_creator", emptyList())
    object SesionData: Destinations("sesion_data",
        listOf(
        navArgument("email"){ type = NavType.StringType },
        navArgument("id"){ type = NavType.StringType }
    ))


}








