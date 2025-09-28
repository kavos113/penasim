package com.example.penasim.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penasim.domain.InningScore
import com.example.penasim.ui.theme.onPrimaryLight
import com.example.penasim.ui.theme.playerBorderColor
import com.example.penasim.ui.theme.primaryLight
import com.example.penasim.ui.theme.substituteBackgroundColor

@Composable
private fun AfterGameContent(
    afterGameInfo: AfterGameInfo,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun InningScoresTable(
    homeInningScores: List<InningScore>,
    awayInningScores: List<InningScore>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        InningHeader(
            modifier = Modifier
                .fillMaxWidth()
        )
        InningScoreRow(
            teamName = "A",
            inningScores = homeInningScores,
            modifier = Modifier
                .fillMaxWidth()
        )
        InningScoreRow(
            teamName = "B",
            inningScores = awayInningScores,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun InningScoreRow(
    teamName: String,
    inningScores: List<InningScore>,
    modifier: Modifier = Modifier
) {
    Row {
        InningScoreItem(
            value = teamName,
            modifier = Modifier
                .width(48.dp)
                .height(32.dp)
        )
        inningScores.forEach { inningScore ->
            InningScoreItem(
                value = inningScore.score.toString(),
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
            )
        }
        InningScoreItem(
            value = inningScores.sumOf { it.score }.toString(),
            modifier = Modifier
                .width(36.dp)
                .height(32.dp)
        )
    }
}

@Composable
private fun InningScoreItem(
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .border(
                width = 1.dp,
                color = playerBorderColor
            )
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun InningHeader(
    modifier: Modifier = Modifier
) {
    Row {
        InningHeaderItem(
            value = "回",
            modifier = Modifier
                .width(48.dp)
                .height(32.dp)
        )
        (1..9).forEach { inning ->
            InningHeaderItem(
                value = inning.toString(),
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
            )
        }
        InningHeaderItem(
            value = "計",
            modifier = Modifier
                .width(36.dp)
                .height(32.dp)
        )
    }
}

@Composable
private fun InningHeaderItem(
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(primaryLight)
            .border(
                width = 1.dp,
                color = playerBorderColor
            )
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            color = onPrimaryLight,
            textAlign = TextAlign.Center
        )
    }
}

private val SAMPLE_INNING_SCORES = listOf(
    InningScore(fixtureId = 0, teamId = 0, inning = 1, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 2, score = 1),
    InningScore(fixtureId = 0, teamId = 0, inning = 3, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 4, score = 2),
    InningScore(fixtureId = 0, teamId = 0, inning = 5, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 6, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 7, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 8, score = 1),
    InningScore(fixtureId = 0, teamId = 0, inning = 9, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 1, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 2, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 3, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 4, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 5, score = 1),
    InningScore(fixtureId = 0, teamId = 1, inning = 6, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 7, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 8, score = 0),
    InningScore(fixtureId = 0, teamId = 1, inning = 9, score = 0),
)

private val SAMPLE_WIN_PITCHER_RESULTS = listOf(
    PitcherResult(
        displayName = "高橋",
        number = 1,
        wins = 12,
        losses = 3,
        holds = 0,
        saves = 0,
        isWin = true,
        isLoss = false,
        isHold = false,
        isSave = false
    ),
    PitcherResult(
        displayName = "田中",
        number = 2,
        wins = 5,
        losses = 2,
        holds = 0,
        saves = 0,
        isWin = false,
        isLoss = false,
        isHold = true,
        isSave = false
    ),
    PitcherResult(
        displayName = "鈴木",
        number = 3,
        wins = 3,
        losses = 4,
        holds = 0,
        saves = 0,
        isWin = false,
        isLoss = false,
        isHold = false,
        isSave = true
    ),
)

private val SAMPLE_LOSE_PITCHER_RESULTS = listOf(
    PitcherResult(
        displayName = "佐藤",
        number = 4,
        wins = 8,
        losses = 7,
        holds = 0,
        saves = 0,
        isWin = false,
        isLoss = true,
        isHold = false,
        isSave = false
    ),
    PitcherResult(
        displayName = "山田",
        number = 5,
        wins = 2,
        losses = 6,
        holds = 0,
        saves = 0,
        isWin = false,
        isLoss = false,
        isHold = false,
        isSave = false
    ),
)

private val SAMPLE_FIELDER_RESULTS = listOf(
    FielderResult(
        displayName = "渡辺",
        inning = 6,
        numberOfHomeRuns = 13
    ),
)

@Preview
@Composable
fun InningScoresTablePreview() {
    InningScoresTable(
        homeInningScores = SAMPLE_INNING_SCORES.take(9),
        awayInningScores = SAMPLE_INNING_SCORES.take(9),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
fun InningScoreRowPreview() {
    InningScoreRow(
        teamName = "A",
        inningScores = SAMPLE_INNING_SCORES.take(9),
        modifier = Modifier
    )
}

@Preview
@Composable
fun AfterGameContentPreview() {
    AfterGameContent(
        afterGameInfo = AfterGameInfo(
            scores = SAMPLE_INNING_SCORES,
            homePitcherResults = SAMPLE_WIN_PITCHER_RESULTS,
            awayPitcherResults = SAMPLE_LOSE_PITCHER_RESULTS,
            homeFielderResults = SAMPLE_FIELDER_RESULTS,
            awayFielderResults = emptyList(),
        )
    )
}