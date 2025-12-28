package com.example.penasim.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.PenasimTheme
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object HomeDestination

@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  onGameClick: () -> Unit = {},
  onNoGameDayClick: () -> Unit = {},
  onCalenderClick: () -> Unit = {},
  onCommandClick: () -> Unit = {},
  homeViewModel: HomeViewModel = hiltViewModel(),
  teamId: Int,
  currentDay: LocalDate,
  lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
  val uiState by homeViewModel.uiState.collectAsState()

  LaunchedEffect(teamId, currentDay) {
    homeViewModel.setTeamId(teamId)
    homeViewModel.setCurrentDay(currentDay)
    homeViewModel.update()
  }

  DisposableEffect(lifeCycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        homeViewModel.update()
      }
    }

    lifeCycleOwner.lifecycle.addObserver(observer)

    onDispose {
      lifeCycleOwner.lifecycle.removeObserver(observer)
    }
  }

  Column(
    modifier = modifier
      .padding(30.dp)
  ) {
    HomeInformation(
      date = uiState.currentDay,
      rank = uiState.rank,
      modifier = Modifier
        .align(alignment = Alignment.CenterHorizontally)
    )
    HomeMenu(
      isGameDay = uiState.isGameDay,
      onGameClick = onGameClick,
      onNoGameDayClick = onNoGameDayClick,
      onCalenderClick = onCalenderClick,
      onCommandClick = onCommandClick,
      modifier = Modifier
    )
  }
}

@Composable
fun HomeInformation(
  date: LocalDate,
  rank: Int,
  modifier: Modifier = Modifier,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier
  ) {
    Text(stringResource(R.string.date, date.month.value, date.dayOfMonth))
    Text(stringResource(R.string.rank, rank))
  }
}

@Composable
fun HomeMenu(
  isGameDay: Boolean = true,
  onGameClick: () -> Unit = {},
  onNoGameDayClick: () -> Unit = {},
  onCalenderClick: () -> Unit = {},
  onCommandClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
  ) {
    if (isGameDay) {
      Button(
        onClick = onGameClick,
        modifier = Modifier
          .fillMaxWidth()
      ) {
        Text(stringResource(R.string.game))
      }
    } else {
      Button(
        onClick = onNoGameDayClick,
        modifier = Modifier
          .fillMaxWidth()
      ) {
        Text(stringResource(R.string.next_day))
      }
    }
    Button(
      onClick = onCalenderClick,
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Text(stringResource(R.string.calender))
    }
    Button(
      onClick = onCommandClick,
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Text(stringResource(R.string.command))
    }
  }
}

@Preview
@Composable
fun HomeScreenPreview() {
  PenasimTheme {
    Surface(
      color = MaterialTheme.colorScheme.background,
      modifier = Modifier
        .fillMaxSize()
    ) {
      HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        teamId = 1,
        currentDay = LocalDate.now()
      )
    }
  }
}