package com.example.login.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.login.admin.news.NewsScreen
import com.example.login.admin.newscreator.NewsCreatorScreen
import com.example.login.presentation.charlist.PersonajeListScreen
import com.example.login.presentation.creator.CreatorScreen
import com.example.login.presentation.creator.CreatorViewModel
import com.example.login.presentation.login.LoginScreen
import com.example.login.presentation.login.LoginViewModel
import com.example.login.presentation.sesion.SesionViewModel
import com.example.login.presentation.chardata.PersonajeDataScreen
import com.example.login.presentation.chardata.CharDataViewModel
import com.example.login.presentation.dice.DiceScreen
import com.example.login.presentation.dice.DiceViewModel
import com.example.login.presentation.registation.RegisterViewModel
import com.example.login.presentation.registation.RegistrationScreen
import com.example.login.presentation.sesion.SesionScreen
import com.example.login.presentation.sesioncreator.SesionCreatorScreen
import com.example.login.presentation.sesioncreator.SesionCreatorViewModel
import com.example.login.presentation.sesiondata.SesionDataScreen
import com.example.login.presentation.sesiondata.SesionDataViewModel
import com.example.login.presentation.spellinfo.SpellInfoScreen
import com.example.login.presentation.spells.SpellsScreen
import com.example.login.presentation.spells.SpellsViewModel
import com.example.login.presentation.users.UsuarioScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Box {
        NavHost(
            navController = navController,
            startDestination = Destinations.Login.route
        ) {
            addLogin(navController)
            addRegister(navController)
            addSesionList(navController)
            addCreator(navController)
            addCharacterList(navController)
            addUserScreen(navController)
            addCharacterData(navController)
            addAdmin(navController)
            addNewsCreator(navController)
            addDice(navController)
            addSpells(navController)
            addSpellInfo(navController)
            addSesionCreator(navController)
            addSesionData(navController)

        }
    }
}

fun NavGraphBuilder.addLogin(
    navController: NavHostController
) {
    composable(
        route = Destinations.Login.route
    ) {
        val viewModel: LoginViewModel = hiltViewModel()
        val email = viewModel.state.value.email

        if (viewModel.state.value.successLogin) {
            LaunchedEffect(key1 = Unit) {
                navController.navigate(
                    Destinations.Sesion.route + "/$email"
                ) {
                    popUpTo(Destinations.Login.route) {
                        inclusive = true
                    }
                }
            }
        } else if (viewModel.state.value.adminLogin) {
            LaunchedEffect(key1 = Unit) {
                navController.navigate(
                    Destinations.Admin.route
                ) {
                    popUpTo(Destinations.Login.route) {
                        inclusive = true
                    }
                }
            }

        }else {
            LoginScreen(
                state = viewModel.state.value,
                onLogin = viewModel::login,
                onNavigateToRegister = {
                    navController.navigate(Destinations.Register.route)
                },
                onDismissDialog = viewModel::hideErrorDialog
            )
        }
    }
}

fun NavGraphBuilder.addRegister(
    navController: NavHostController
) {
    composable(
        route = Destinations.Register.route
    ) {
        val viewModel: RegisterViewModel = hiltViewModel()

        RegistrationScreen(
            state = viewModel.state.value,
            onRegister = viewModel::register,
            onBack = {
                navController.popBackStack()
            },
            onDismissDialog = viewModel::hideErrorDialog
        )
    }
}


fun NavGraphBuilder.addSesionList(navController: NavHostController) {
    composable(
        route = Destinations.Sesion.route + "/{email}",
        arguments = Destinations.Sesion.arguments
    ) { backStackEntry ->
        val viewModel: SesionViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }

        SesionScreen(
            onNavigateToList = {
                navController.navigate(Destinations.Character.route+"/$emailArg")
            },
            onNavigateToDice = {
                navController.navigate(Destinations.Dice.route+"/$emailArg")
            },
            onNavigateToUser = {
                navController.navigate(Destinations.User.route+"/$emailArg")
            },
            onNavigateToHome = {
                navController.navigate(Destinations.Sesion.route+"/$emailArg")
            },
            onNavigateToSpells = {
                navController.navigate(Destinations.Spells.route+"/$emailArg")
            },
            onBack = {
                navController.popBackStack()
            },
            onNavigateToCreator = {
                navController.navigate(Destinations.SesionCreator.route+"/$emailArg")
            },
            onNavigateToData = { sesionId ->
                navController.navigate("sesion_data/$sesionId/$emailArg")
            },
            email = emailArg
        )
    }
}


fun NavGraphBuilder.addCreator(navController: NavHostController) {
    composable(
        route = Destinations.Creator.route + "/{email}",
        arguments = Destinations.Creator.arguments
    ) { backStackEntry ->
        val viewModel: CreatorViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }
        CreatorScreen(email = emailArg,
            onNavigateToList = {navController.navigate(Destinations.Character.route+"/$emailArg")})
    }
}

fun NavGraphBuilder.addCharacterList(navController: NavHostController) {
    composable(route = Destinations.Character.route+"/{email}",
        arguments = Destinations.Character.arguments)
        { backStackEntry ->
            val viewModel: CreatorViewModel = hiltViewModel()
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""

            LaunchedEffect(emailArg) {
                viewModel.setEmail(emailArg)
            }
            PersonajeListScreen(email = emailArg,
                onNavigateToCreator = {navController.navigate(Destinations.Creator.route+"/$emailArg")},
                onNavigateToList = {navController.navigate(Destinations.Character.route+"/$emailArg")},
                onNavigateToUser = {navController.navigate(Destinations.User.route+"/$emailArg")},
                onNavigateToHome = {navController.navigate(Destinations.Sesion.route+"/$emailArg")},
                onNavigateToData = { personajeId -> navController.navigate("char_data/$personajeId")},
                onNavigateToDice = {navController.navigate(Destinations.Dice.route+"/$emailArg")},
                onNavigateToSpells = {navController.navigate(Destinations.Spells.route+"/$emailArg")},
                onBack = { navController.popBackStack() })
    }
}
fun NavGraphBuilder.addSpells(navController: NavHostController) {
    composable(route = Destinations.Spells.route + "/{email}",
        arguments = Destinations.Spells.arguments)
    { backStackEntry ->
        val viewModel: SpellsViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }

        SpellsScreen(
            email = emailArg,
            onNavigateToList = { navController.navigate(Destinations.Character.route + "/$emailArg") },
            onNavigateToUser = { navController.navigate(Destinations.User.route + "/$emailArg") },
            onNavigateToHome = { navController.navigate(Destinations.Sesion.route + "/$emailArg") },
            onNavigateToDice = { navController.navigate(Destinations.Dice.route + "/$emailArg") },
            onNavigateToSpells = { navController.navigate(Destinations.Spells.route + "/$emailArg") },
            onNavigateToSpellInfo = { name, level, school, classes, description ->
                navController.navigate(
                    Destinations.SpellInfo.route +
                            "/$emailArg" +
                            "/$name" +
                            "/$level" +
                            "/$school" +
                            "/$classes" +
                            "/$description"
                )
            },
            onBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.addUserScreen(navController: NavHostController) {
    composable(route = Destinations.User.route+"/{email}",
        arguments = Destinations.User.arguments) {
            backStackEntry ->
        val viewModel: CreatorViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }

        UsuarioScreen(
            onNavigateToSingOut = { navController.navigate(Destinations.Login.route){
                popUpTo(0) { inclusive = true }
                FirebaseAuth.getInstance().signOut()
            } },
            onNavigateToHome = {navController.navigate(Destinations.Sesion.route+"/$emailArg")},
            email = emailArg)
    }
}
fun NavGraphBuilder.addCharacterData(navController: NavHostController) {
    composable(
        route = "char_data/{id}"
    ) { backStackEntry ->
        val charId = backStackEntry.arguments?.getString("id") ?: ""
        val viewModel: CharDataViewModel = hiltViewModel()

        val emailArg = backStackEntry.arguments?.getString("email") ?: ""
        LaunchedEffect(charId) {
            viewModel.leerDatosPorId(charId) {}
            viewModel.setEmail(emailArg)
        }

        PersonajeDataScreen(
            onNavigateToUser = { navController.navigate(Destinations.User.route+"/$emailArg")},
            charId = charId,
            onBack = { navController.popBackStack() }
        )
    }
}
fun NavGraphBuilder.addAdmin(navController: NavHostController) {
    composable(route = Destinations.Admin.route, arguments = Destinations.Admin.arguments){
        NewsScreen(onNavigateToNewsCreator = { navController.navigate(Destinations.NewsCreator.route, navOptions = null) },
            onNavigateToSingOut = { navController.navigate(Destinations.Login.route){
                popUpTo(0) { inclusive = true }
                FirebaseAuth.getInstance().signOut()
            } })

    }
}
fun NavGraphBuilder.addNewsCreator(navController: NavHostController) {
    composable(route = Destinations.NewsCreator.route, arguments = Destinations.NewsCreator.arguments){
        NewsCreatorScreen(onBack = { navController.popBackStack() })
    }
}
fun NavGraphBuilder.addDice(navController: NavHostController) {
    composable(
        route = Destinations.Dice.route + "/{email}",
        arguments = Destinations.Sesion.arguments
    ) { backStackEntry ->
        val viewModel: DiceViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }

        DiceScreen(
            onNavigateToList = {
                navController.navigate(Destinations.Character.route+"/$emailArg")
            },
            onNavigateToDice = {
                navController.navigate(Destinations.Dice.route+"/$emailArg")
            },
            onNavigateToUser = {
                navController.navigate(Destinations.User.route+"/$emailArg")
            },
            onNavigateToHome = {
                navController.navigate(Destinations.Sesion.route+"/$emailArg")
            },
            onBack = {
                navController.popBackStack()
            },
            onNavigateToSpells = {
                navController.navigate(Destinations.Spells.route+"/$emailArg")
            },
            email = emailArg
        )
    }
}
fun NavGraphBuilder.addSpellInfo(navController: NavHostController) {
    composable(
        route = Destinations.SpellInfo.route +
                "/{email}/{name}/{level}/{school}/{classes}/{description}",
        arguments = listOf(
            navArgument("email") { type = NavType.StringType },
            navArgument("name") { type = NavType.StringType },
            navArgument("level") { type = NavType.StringType },
            navArgument("school") { type = NavType.StringType },
            navArgument("classes") { type = NavType.StringType },
            navArgument("description") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val viewModel: SpellsViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }

        val name = backStackEntry.arguments?.getString("name") ?: ""
        val level = backStackEntry.arguments?.getString("level") ?: ""
        val school = backStackEntry.arguments?.getString("school") ?: ""
        val classes = backStackEntry.arguments?.getString("classes") ?: ""
        val description = backStackEntry.arguments?.getString("description") ?: ""

        SpellInfoScreen(
            name = name,
            level = level,
            school = school,
            classes = classes,
            description = description,
            onBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.addSesionCreator(navController: NavHostController) {
    composable(
        route = Destinations.SesionCreator.route + "/{email}",
        arguments = Destinations.SesionCreator.arguments
    ) { backStackEntry ->
        val viewModel: SesionCreatorViewModel = hiltViewModel()
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""

        LaunchedEffect(emailArg) {
            viewModel.setEmail(emailArg)
        }
        SesionCreatorScreen(email = emailArg,
            onNavigateToList = {navController.navigate(Destinations.Sesion.route+"/$emailArg")},
            onBack = {navController.popBackStack()})
    }
}

fun NavGraphBuilder.addSesionData(navController: NavHostController) {
    composable(
        route = "sesion_data/{sesionId}/{email}",
        arguments = listOf(
            navArgument("sesionId") { type = NavType.StringType },
            navArgument("email") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val sesionId = backStackEntry.arguments?.getString("sesionId") ?: ""
        val emailArg = backStackEntry.arguments?.getString("email") ?: ""
        val viewModel: SesionDataViewModel = hiltViewModel()

        LaunchedEffect(sesionId, emailArg) {
            viewModel.setEmail(emailArg)
            viewModel.loadSesionData(sesionId)
        }

        SesionDataScreen(
            sesionId = sesionId,
            email = emailArg,
            onBack = { navController.popBackStack() }
        )
    }
}







