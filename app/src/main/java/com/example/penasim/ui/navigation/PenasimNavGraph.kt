package com.example.penasim.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.penasim.ui.calender.CalendarScreen
import com.example.penasim.ui.calender.CalenderDestination
import com.example.penasim.ui.command.CommandDestination
import com.example.penasim.ui.command.CommandScreen
import com.example.penasim.ui.game.AfterGameDestination
import com.example.penasim.ui.game.AfterGameScreen
import com.example.penasim.ui.game.AfterGameScreenWithoutGameResult
import com.example.penasim.ui.game.AfterGameWithoutGameResultDestination
import com.example.penasim.ui.game.BeforeGameDestination
import com.example.penasim.ui.game.BeforeGameScreen
import com.example.penasim.ui.game.GameViewModel
import com.example.penasim.ui.home.HomeDestination
import com.example.penasim.ui.home.HomeScreen

@Composable
fun PenasimNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val globalViewModel: GlobalViewModel = viewModel()
    val globalState = globalViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onGameClick = { navController.navigate(BeforeGameDestination.route) },
                onNoGameDayClick = { navController.navigate(AfterGameWithoutGameResultDestination.route) },
                onCalenderClick = { navController.navigate(CalenderDestination.route) },
                onCommandClick = { navController.navigate(CommandDestination.route) },
                modifier = Modifier.fillMaxSize(),
                teamId = globalState.value.teamId,
                currentDay = globalState.value.currentDay
            )
        }
        navigation(
            startDestination = BeforeGameDestination.route,
            route = "game_graph"
        ) {
            composable(route = BeforeGameDestination.route) { navBackStackEntry ->
                val parentEntity = remember(navBackStackEntry) {
                    navController.getBackStackEntry("game_graph")
                }
                val gameViewModel: GameViewModel = hiltViewModel(parentEntity)
                BeforeGameScreen(
                    gameViewModel = gameViewModel,
                    navToAfterGame = { navController.navigate(route = AfterGameDestination.route) },
                    modifier = Modifier.fillMaxSize(),
                    currentDay = globalState.value.currentDay
                )
            }
            composable(route = AfterGameDestination.route) {
                val parentEntity = remember(it) {
                    navController.getBackStackEntry("game_graph")
                }
                val gameViewModel: GameViewModel = hiltViewModel(parentEntity)
                AfterGameScreen(
                    gameViewModel = gameViewModel,
                    onClickFinish = {
                        navController.navigate(HomeDestination.route)
                        globalViewModel.nextDay()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable(route = AfterGameWithoutGameResultDestination.route) {
            AfterGameScreenWithoutGameResult(
                gameViewModel = hiltViewModel(),
                onClickFinish = {
                    navController.navigate(HomeDestination.route)
                    globalViewModel.nextDay()
                },
                modifier = Modifier.fillMaxSize(),
                currentDay = globalState.value.currentDay
            )
        }
        composable(route = CalenderDestination.route) {
            CalendarScreen(currentDay = globalState.value.currentDay)
        }
        composable(route = CommandDestination.route) {
            CommandScreen(teamId = globalState.value.teamId)
        }
    }
}