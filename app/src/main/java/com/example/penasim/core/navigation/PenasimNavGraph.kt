package com.example.penasim.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.penasim.features.schedule.ui.calender.CalendarDestination
import com.example.penasim.features.schedule.ui.calender.CalendarScreen
import com.example.penasim.features.command.ui.command.CommandDestination
import com.example.penasim.features.command.ui.command.CommandScreen
import com.example.penasim.features.game.ui.after.AfterGameDestination
import com.example.penasim.features.game.ui.after.AfterGameScreen
import com.example.penasim.features.game.ui.after.AfterGameScreenWithoutGameResult
import com.example.penasim.features.game.ui.after.AfterGameWithoutGameResultDestination
import com.example.penasim.features.game.ui.before.BeforeGameDestination
import com.example.penasim.features.game.ui.before.BeforeGameScreen
import com.example.penasim.features.game.ui.ingame.InGameDestination
import com.example.penasim.features.game.ui.ingame.InGameScreen
import com.example.penasim.features.home.ui.home.HomeDestination
import com.example.penasim.features.home.ui.home.HomeScreen

@Composable
fun PenasimNavHost(
  navController: NavHostController,
  modifier: Modifier = Modifier,
) {
  val globalViewModel: GlobalViewModel = viewModel()
  val globalState = globalViewModel.state.collectAsState()

  NavHost(
    navController = navController,
    startDestination = HomeDestination,
    modifier = modifier
  ) {
    composable<HomeDestination> {
      HomeScreen(
        onGameClick = { navController.navigate(BeforeGameDestination) },
        onNoGameDayClick = { navController.navigate(AfterGameWithoutGameResultDestination) },
        onCalenderClick = { navController.navigate(CalendarDestination) },
        onCommandClick = { navController.navigate(CommandDestination) },
        modifier = Modifier.fillMaxSize(),
        teamId = globalState.value.teamId,
        currentDay = globalState.value.currentDay
      )
    }
    composable<BeforeGameDestination> {
      BeforeGameScreen(
        viewModel = hiltViewModel(),
        navToAfterGame = { navController.navigate(route = AfterGameDestination(isSkipped = true)) },
        navToInGame = { navController.navigate(InGameDestination) },
        modifier = Modifier.fillMaxSize(),
        currentDay = globalState.value.currentDay
      )
    }
    composable<AfterGameDestination> { backStackEntry ->
      val afterGameDestination: AfterGameDestination = backStackEntry.toRoute()
      AfterGameScreen(
        viewModel = hiltViewModel(),
        onClickFinish = {
          navController.navigate(HomeDestination)
          globalViewModel.nextDay()
        },
        modifier = Modifier.fillMaxSize(),
        currentDay = globalState.value.currentDay,
        isSkipped = afterGameDestination.isSkipped
      )
    }
    composable<AfterGameWithoutGameResultDestination> {
      AfterGameScreenWithoutGameResult(
        viewModel = hiltViewModel(),
        onClickFinish = {
          navController.navigate(HomeDestination)
          globalViewModel.nextDay()
        },
        modifier = Modifier.fillMaxSize(),
        currentDay = globalState.value.currentDay
      )
    }
    composable<InGameDestination> {
      InGameScreen(
        viewModel = hiltViewModel(),
        onGameFinish = { navController.navigate(AfterGameDestination(isSkipped = false)) },
        currentDay = globalState.value.currentDay
      )
    }
    composable<CalendarDestination> {
      CalendarScreen(
        currentDay = globalState.value.currentDay,
        onNextGame = { globalViewModel.nextDay() }
      )
    }
    composable<CommandDestination> {
      CommandScreen(teamId = globalState.value.teamId)
    }
  }
}