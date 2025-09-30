package com.example.penasim.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penasim.R
import com.example.penasim.const.DataSource
import com.example.penasim.domain.InningScore
import com.example.penasim.ui.common.Ranking
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.errorContainerLight
import com.example.penasim.ui.theme.onPrimaryLight
import com.example.penasim.ui.theme.playerBorderColor
import com.example.penasim.ui.theme.primaryContainerLight
import com.example.penasim.ui.theme.primaryLight
import java.time.LocalDate

object AfterGameDestination : NavigationDestination {
    override val route: String = "after_game"
    override val titleResId: Int = R.string.game
}

@Composable
fun AfterGameScreen(
    modifier: Modifier = Modifier,
    navFinishGame: () -> Unit = { },
    gameViewModel: GameViewModel,
) {
    val uiState by gameViewModel.uiState.collectAsState()

    BackHandler(
        enabled = true,
        onBack = { /* Do nothing */ }
    )

    AfterGameContent(
        date = uiState.date,
        afterGameInfo = uiState.afterGameInfo,
        modifier = modifier,
        onClickFinish = navFinishGame
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AfterGameContent(
    date: LocalDate,
    afterGameInfo: AfterGameInfo,
    modifier: Modifier = Modifier,
    onClickFinish: () -> Unit = { }
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "${date.monthValue}月${date.dayOfMonth}日",
                        fontSize = 20.sp,
                        modifier = Modifier
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            InningScoresTable(
                homeInningScores = afterGameInfo.homeScores,
                awayInningScores = afterGameInfo.awayScores,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                SingleTeamPitcherResults(
                    pitcherResults = afterGameInfo.awayPitcherResults,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
                VerticalDivider(
                    color = playerBorderColor,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                )
                SingleTeamPitcherResults(
                    pitcherResults = afterGameInfo.homePitcherResults,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }

            HorizontalDivider(
                color = playerBorderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                SingleTeamFielderResults(
                    fielderResults = afterGameInfo.awayFielderResults,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
                VerticalDivider(
                    color = playerBorderColor,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                )
                SingleTeamFielderResults(
                    fielderResults = afterGameInfo.homeFielderResults,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Ranking(
                rankings = afterGameInfo.rankings,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 32.dp)
            )

            Button(
                onClick = onClickFinish,
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "終了")
            }
        }
    }
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
            inningScores = awayInningScores,
            modifier = Modifier
                .fillMaxWidth()
        )
        InningScoreRow(
            teamName = "B",
            inningScores = homeInningScores,
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

@Composable
private fun SingleTeamPitcherResults(
    pitcherResults: List<PitcherResult>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        pitcherResults.forEach { pitcherResult ->
            PitcherResultItem(
                pitcherResult = pitcherResult,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

private fun displayPitcherResults(pitcherResult: PitcherResult): String {
    val sb = StringBuilder()
    sb.append(pitcherResult.displayName)
    if (pitcherResult.wins > 0) {
        sb.append(" ${pitcherResult.wins}勝")
    }
    if (pitcherResult.losses > 0) {
        sb.append(" ${pitcherResult.losses}敗")
    }
    if (pitcherResult.holds > 0) {
        sb.append(" ${pitcherResult.holds}H")
    }
    if (pitcherResult.saves > 0) {
        sb.append(" ${pitcherResult.saves}S")
    }
    return sb.toString()
}

@Composable
private fun PitcherResultItem(
    pitcherResult: PitcherResult,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (pitcherResult.isWin) {
            Text(
                text = "勝",
                fontSize = 16.sp,
                modifier = Modifier
                    .background(errorContainerLight)
                    .padding(horizontal = 4.dp)
            )
        } else if (pitcherResult.isLoss) {
            Text(
                text = "敗",
                fontSize = 16.sp,
                modifier = Modifier
                    .background(primaryContainerLight)
                    .padding(horizontal = 4.dp)
            )
        } else if (pitcherResult.isHold) {
            Text(
                text = "Ｈ",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            )
        } else if (pitcherResult.isSave) {
            Text(
                text = "Ｓ",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            )
        } else {
            Text(
                text = "　",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            )
        }
        Text(
            text = displayPitcherResults(pitcherResult),
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun SingleTeamFielderResults(
    fielderResults: List<FielderResult>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        fielderResults.forEach { fielderResult ->
            FielderResultItem(
                fielderResult = fielderResult,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FielderResultItem(
    fielderResult: FielderResult,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${fielderResult.displayName} ${fielderResult.inning}回 ${fielderResult.numberOfHomeRuns}号",
        fontSize = 16.sp,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

private val SAMPLE_HOME_INNING_SCORES = listOf(
    InningScore(fixtureId = 0, teamId = 0, inning = 1, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 2, score = 1),
    InningScore(fixtureId = 0, teamId = 0, inning = 3, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 4, score = 2),
    InningScore(fixtureId = 0, teamId = 0, inning = 5, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 6, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 7, score = 0),
    InningScore(fixtureId = 0, teamId = 0, inning = 8, score = 1),
    InningScore(fixtureId = 0, teamId = 0, inning = 9, score = 0),
)

private val SAMPLE_AWAY_INNING_SCORES = listOf(
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
        holds = 14,
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
        saves = 25,
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
        homeInningScores = SAMPLE_HOME_INNING_SCORES,
        awayInningScores = SAMPLE_AWAY_INNING_SCORES,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
fun InningScoreRowPreview() {
    InningScoreRow(
        teamName = "A",
        inningScores = SAMPLE_HOME_INNING_SCORES,
        modifier = Modifier
    )
}

@Preview
@Composable
fun SingleTeamPitcherResultsPreview() {
    SingleTeamPitcherResults(
        pitcherResults = SAMPLE_WIN_PITCHER_RESULTS,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
fun SingleTeamFielderResultsPreview() {
    SingleTeamFielderResults(
        fielderResults = SAMPLE_FIELDER_RESULTS,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
fun AfterGameContentPreview() {
    AfterGameContent(
        date = LocalDate.of(2024, 4, 1),
        afterGameInfo = AfterGameInfo(
            homeScores = SAMPLE_HOME_INNING_SCORES,
            awayScores = SAMPLE_AWAY_INNING_SCORES,
            homePitcherResults = SAMPLE_WIN_PITCHER_RESULTS,
            awayPitcherResults = SAMPLE_LOSE_PITCHER_RESULTS,
            homeFielderResults = SAMPLE_FIELDER_RESULTS,
            awayFielderResults = emptyList(),
            rankings = DataSource.rankings
        )
    )
}