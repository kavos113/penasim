package com.example.penasim.ui.calender

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.penasim.R
import com.example.penasim.const.Constants
import com.example.penasim.ui.common.Clause
import com.example.penasim.ui.common.Ranking
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.PenasimTheme
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CalenderDestination : NavigationDestination {
    override val route: String = "calendar"
    override val titleResId: Int = R.string.calender
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    currentDay: LocalDate
) {
    val uiState by calendarViewModel.uiState.collectAsState()

    LaunchedEffect(currentDay) {
        calendarViewModel.setCurrentDay(currentDay)
    }

    CalendarContent(
        uiState = uiState,
        onNextGame = { calendarViewModel.nextGame() },
        modifier = modifier
    )
}

@Composable
private fun CalendarContent(
    uiState: CalendarUiState,
    onNextGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.currentDay) {
        if (uiState.currentDay >= Constants.START.plusDays(4)) {
            listState.animateScrollToItem(
                index = (ChronoUnit.DAYS.between(Constants.START, uiState.currentDay) - 2).toInt(),
                scrollOffset = 0
            )
        }
    }

    Box {
        Column(modifier = modifier) {
            Ranking(rankings = uiState.rankings)
            Spacer(modifier = Modifier.size(16.dp))
            LazyColumn(
                state = listState
            ) {
                items(
                    items = uiState.games.entries.sortedBy { it.key }.toList()
                ) { game ->
                    Clause(
                        currentDay = game.key,
                        games = game.value,
                        modifier = Modifier
                            .padding(8.dp),
                        focusTeamIcon = R.drawable.team1_icon // TODO fix
                    )
                }
            }
        }
        NextGameButton(
            onClick = onNextGame,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun NextGameButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next Game"
        )
    }
}

@Preview
@Composable
fun PreviewCalendarScreen() {
    PenasimTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
        ) {
            CalendarContent(
                uiState = CalendarUiState(),
                onNextGame = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}