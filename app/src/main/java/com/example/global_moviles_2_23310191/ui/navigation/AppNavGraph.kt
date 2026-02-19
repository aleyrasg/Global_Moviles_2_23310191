package com.example.global_moviles_2_23310191.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.global_moviles_2_23310191.ui.auth.AuthViewModel
import com.example.global_moviles_2_23310191.ui.auth.LoginScreen
import com.example.global_moviles_2_23310191.ui.auth.RegisterScreen
import com.example.global_moviles_2_23310191.ui.home.HomeScreen
import com.example.global_moviles_2_23310191.ui.map.MapScreen
import com.example.global_moviles_2_23310191.ui.places.PlaceFormScreen
import com.example.global_moviles_2_23310191.ui.places.PlaceListScreen
import com.example.global_moviles_2_23310191.ui.progress.ProgressFormScreen
import com.example.global_moviles_2_23310191.ui.progress.ProgressListScreen
import com.example.global_moviles_2_23310191.ui.routines.RoutineFormScreen
import com.example.global_moviles_2_23310191.ui.routines.RoutineListScreen

@Composable
fun AppNavGraph(
    startDestination: String,
    vm: AuthViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) { LoginScreen(navController, vm) }
        composable(Routes.REGISTER) { RegisterScreen(navController, vm) }
        composable(Routes.HOME) { HomeScreen(navController, vm) }

        composable(Routes.ROUTINES) { RoutineListScreen(navController) }

        composable(Routes.ROUTINE_FORM) {
            RoutineFormScreen(navController, routineId = null)
        }

        composable(
            route = "${Routes.ROUTINE_FORM}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            RoutineFormScreen(navController, routineId = id)
        }

        composable(Routes.PLACES) { PlaceListScreen(navController) }

        composable(
            route = "${Routes.PLACE_FORM}?lat={lat}&lng={lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                navArgument("lng") { type = NavType.FloatType; defaultValue = 0f }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
            val lng = backStackEntry.arguments?.getFloat("lng") ?: 0f
            PlaceFormScreen(
                navController = navController,
                placeId = null,
                initialLat = if (lat != 0f) lat.toDouble() else null,
                initialLng = if (lng != 0f) lng.toDouble() else null
            )
        }

        composable(Routes.PLACE_FORM) {
            PlaceFormScreen(navController, placeId = null)
        }

        composable(
            route = "${Routes.PLACE_FORM}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            PlaceFormScreen(navController, placeId = id)
        }

        composable(Routes.MAP) { MapScreen(navController) }

        composable(Routes.PROGRESS) { ProgressListScreen(navController) }

        composable(Routes.PROGRESS_FORM) { ProgressFormScreen(navController) }
    }
}
