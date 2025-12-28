package com.example.penasim.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.penasim.ui.game.InGameDestination
import com.example.penasim.ui.game.InGameScreen
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
    composable(route = BeforeGameDestination.route) {
      BeforeGameScreen(
        viewModel = hiltViewModel(),
        navToAfterGame = { navController.navigate(route = AfterGameDestination.route) },
        navToInGame = { navController.navigate(InGameDestination.route) },
        modifier = Modifier.fillMaxSize(),
        currentDay = globalState.value.currentDay
      )
    }
    composable(route = AfterGameDestination.route) {
      AfterGameScreen(
        viewModel = hiltViewModel(),
        onClickFinish = {
          navController.navigate(HomeDestination.route)
          globalViewModel.nextDay()
        },
        modifier = Modifier.fillMaxSize(),
        currentDay = globalState.value.currentDay,
        isSkipped = true
      )
    }
    composable(route = AfterGameWithoutGameResultDestination.route) {
      AfterGameScreenWithoutGameResult(
        viewModel = hiltViewModel(),
        onClickFinish = {
          navController.navigate(HomeDestination.route)
          globalViewModel.nextDay()
        },
        modifier = Modifier.fillMaxSize(),
        currentDay = globalState.value.currentDay
      )
    }
    composable(route = InGameDestination.route) {
      InGameScreen(
        viewModel = hiltViewModel(),
        onGameFinish = { navController.navigate(AfterGameDestination.route) },
        currentDay = globalState.value.currentDay
      )
    }
    composable(route = CalenderDestination.route) {
      CalendarScreen(
        currentDay = globalState.value.currentDay,
        onNextGame = { globalViewModel.nextDay() }
      )
    }
    composable(route = CommandDestination.route) {
      CommandScreen(teamId = globalState.value.teamId)
    }
  }
}