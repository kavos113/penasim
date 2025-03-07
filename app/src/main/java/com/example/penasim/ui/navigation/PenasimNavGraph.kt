package com.example.penasim.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.penasim.ui.calender.CalendarScreen
import com.example.penasim.ui.calender.CalenderDestination
import com.example.penasim.ui.command.CommandDestination
import com.example.penasim.ui.command.CommandScreen
import com.example.penasim.ui.game.GameDestination
import com.example.penasim.ui.game.GameScreen
import com.example.penasim.ui.home.HomeDestination
import com.example.penasim.ui.home.HomeScreen

@Composable
fun PenasimNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onGameClick = { navController.navigate(GameDestination.route) },
                onCalenderClick = { navController.navigate(CalenderDestination.route) },
                onCommandClick = { navController.navigate(CommandDestination.route) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable(route = GameDestination.route) {
            GameScreen()
        }
        composable(route = CalenderDestination.route) {
            CalendarScreen()
        }
        composable(route = CommandDestination.route) {
            CommandScreen()
        }
    }
}