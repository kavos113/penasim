package com.example.penasim.ui.calender

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.penasim.R
import com.example.penasim.const.DateConst
import com.example.penasim.domain.toLeague
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
) {
    val uiState by calendarViewModel.uiState.collectAsState()
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
        if (uiState.currentDay >= DateConst.START.plusDays(4)) {
            listState.animateScrollToItem(
                index = (ChronoUnit.DAYS.between(DateConst.START, uiState.currentDay) - 2).toInt(),
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
                            .padding(8.dp)
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
private fun Ranking(
    rankings: List<RankingUiInfo>,
    modifier: Modifier = Modifier
) {
    assert(rankings.size == 12)
    Column(
        modifier = modifier
    ) {
        repeat(2) {
            LeagueRanking(
                rankings = rankings.filter { ranking -> ranking.league == it.toLeague() },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LeagueRanking(
    rankings: List<RankingUiInfo>,
    modifier: Modifier = Modifier
) {
    assert(rankings.size == 6)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(6) {
            RankingTeam(
                teamLogo = rankings[it].teamIcon,
                gamesBack = rankings[it].gameBack,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun RankingTeam(
    @DrawableRes teamLogo: Int,
    gamesBack: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = teamLogo),
            contentDescription = "team icon",
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = gamesBack.toString(),
            modifier = Modifier
        )
    }
}

@Composable
private fun Game(
    modifier: Modifier = Modifier,
    homeTeamLogo: Int,
    awayTeamLogo: Int,
    homeTeamScore: Int,
    awayTeamScore: Int,
    isGameFinished: Boolean = false,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier
            .padding(
                top = 4.dp,
                bottom = 4.dp
            )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
        ) {
            Image(
                painter = painterResource(id = homeTeamLogo),
                contentDescription = "home team icon",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = if (isGameFinished) homeTeamScore.toString() else " ",
                modifier = Modifier
            )
            Text(
                text = "-",
                modifier = Modifier
            )
            Text(
                text = if (isGameFinished) awayTeamScore.toString() else " ",
                modifier = Modifier
            )
            Image(
                painter = painterResource(id = awayTeamLogo),
                contentDescription = "away team icon",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun Clause(
    currentDay: LocalDate,
    games: List<GameUiInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "$currentDay",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            repeat(2) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) { j ->
                        if (i * 3 + j >= games.size) return@repeat
                        val game = games[i * 3 + j]
                        Game(
                            homeTeamLogo = game.homeTeamIcon,
                            awayTeamLogo = game.awayTeamIcon,
                            homeTeamScore = game.homeTeamScore,
                            awayTeamScore = game.awayTeamScore,
                            isGameFinished = game.isGameFinished
                        )
                    }
                }
            }
        }
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