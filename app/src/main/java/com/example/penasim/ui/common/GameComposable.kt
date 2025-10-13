package com.example.penasim.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.penasim.R
import java.time.LocalDate


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
                painter = painterResource(id = awayTeamLogo),
                contentDescription = "away team icon",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = if (isGameFinished) awayTeamScore.toString() else " ",
                modifier = Modifier
            )
            Text(
                text = "-",
                modifier = Modifier
            )
            Text(
                text = if (isGameFinished) homeTeamScore.toString() else " ",
                modifier = Modifier
            )
            Image(
                painter = painterResource(id = homeTeamLogo),
                contentDescription = "home team icon",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun Clause(
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

@Preview
@Composable
fun PreviewClause() {
    Clause(
        currentDay = LocalDate.of(2024, 6, 15),
        games = listOf(
            GameUiInfo(
                homeTeamIcon = R.drawable.team1_icon,
                homeTeamScore = 3,
                awayTeamIcon = R.drawable.team2_icon,
                awayTeamScore = 1,
                isGameFinished = true
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team3_icon,
                homeTeamScore = 2,
                awayTeamIcon = R.drawable.team4_icon,
                awayTeamScore = 2,
                isGameFinished = true
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team5_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team6_icon,
                awayTeamScore = 1,
                isGameFinished = true
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team7_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team8_icon,
                awayTeamScore = 3,
                isGameFinished = true
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team9_icon,
                homeTeamScore = 1,
                awayTeamIcon = R.drawable.team10_icon,
                awayTeamScore = 0,
                isGameFinished = true
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team11_icon,
                homeTeamScore = 2,
                awayTeamIcon = R.drawable.team12_icon,
                awayTeamScore = 2,
                isGameFinished = true
            )
        ),
        modifier = Modifier
            .padding(16.dp)
    )
}

@Preview
@Composable
fun PreviewClauseWithFewGames() {
    Clause(
        currentDay = LocalDate.of(2024, 6, 15),
        games = listOf(
            GameUiInfo(
                homeTeamIcon = R.drawable.team1_icon,
                homeTeamScore = 3,
                awayTeamIcon = R.drawable.team2_icon,
                awayTeamScore = 1,
                isGameFinished = true
            ),
        ),
        modifier = Modifier
            .padding(16.dp)
    )
}

@Preview
@Composable
fun PreviewNotFinishedClause() {
    Clause(
        currentDay = LocalDate.of(2024, 6, 15),
        games = listOf(
            GameUiInfo(
                homeTeamIcon = R.drawable.team1_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team2_icon,
                awayTeamScore = 0,
                isGameFinished = false
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team3_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team4_icon,
                awayTeamScore = 0,
                isGameFinished = false
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team5_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team6_icon,
                awayTeamScore = 0,
                isGameFinished = false
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team7_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team8_icon,
                awayTeamScore = 0,
                isGameFinished = false
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team9_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team10_icon,
                awayTeamScore = 0,
                isGameFinished = false
            ),
            GameUiInfo(
                homeTeamIcon = R.drawable.team11_icon,
                homeTeamScore = 0,
                awayTeamIcon = R.drawable.team12_icon,
                awayTeamScore = 0,
                isGameFinished = false
            )
        ),
        modifier = Modifier
            .padding(16.dp)
    )
}