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
import com.example.global_moviles_2_23310191.ui.reminders.ReminderScreen
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

        composable(Routes.MAP) { MapScreen() }

        // ✅ FIX: pasar navController
        composable(Routes.REMINDERS) { ReminderScreen(navController) }
    }
}
