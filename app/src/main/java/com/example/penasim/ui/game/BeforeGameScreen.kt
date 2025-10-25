package com.example.penasim.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.sp
import com.example.penasim.R
import com.example.penasim.const.icon
import com.example.penasim.domain.League
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamStanding
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.OrderPlayerItem
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor
import java.time.LocalDate

object BeforeGameDestination : NavigationDestination {
    override val route: String = "before_game"
    override val titleResId: Int = R.string.game
}

@Composable
fun BeforeGameScreen(
    modifier: Modifier = Modifier,
    navToAfterGame: () -> Unit = {},
    gameViewModel: GameViewModel,
    currentDay: LocalDate
) {
    val uiState by gameViewModel.uiState.collectAsState()

    LaunchedEffect(currentDay) {
        gameViewModel.setDate(currentDay)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BeforeGameContent(
            date = uiState.date,
            beforeGameInfo = uiState.beforeGameInfo,
            onClickStartGame = {
                gameViewModel.startGame()
                navToAfterGame()
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeforeGameContent(
    date: LocalDate,
    beforeGameInfo: BeforeGameInfo,
    modifier: Modifier = Modifier,
    onClickStartGame: () -> Unit = { },
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
            modifier = Modifier
                .padding(innerPadding)
                .then(modifier)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamInfoView(
                    teamStanding = beforeGameInfo.awayTeam,
                    fielders = beforeGameInfo.awayStartingPlayers,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )
                TeamInfoView(
                    teamStanding = beforeGameInfo.homeTeam,
                    fielders = beforeGameInfo.homeStartingPlayers,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )
            }
            Button(
                onClick = onClickStartGame,
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "試合開始")
            }
        }
    }
}

@Composable
private fun TeamInfoView(
    teamStanding: TeamStanding,
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = teamStanding.team.icon()),
            contentDescription = "team icon",
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "${teamStanding.rank}位",
            fontSize = 24.sp
        )
        OrderList(
            fielders = fielders,
            modifier = Modifier
        )
    }
}

@Composable
private fun OrderList(
    fielders: List<DisplayFielder>,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        repeat(fielders.size) {
            OrderPlayerItem(
                player = fielders[it],
                modifier = Modifier
            )
        }
    }
}

private val SAMPLE_FIELDERS = listOf(
    DisplayFielder(
        id = 1,
        displayName = "山田",
        position = Position.CENTER_FIELDER,
        number = 1,
        color = outfielderColor
    ),
    DisplayFielder(
        id = 2,
        displayName = "鈴木",
        position = Position.LEFT_FIELDER,
        number = 2,
        color = outfielderColor
    ),
    DisplayFielder(
        id = 3,
        displayName = "田中",
        position = Position.FIRST_BASEMAN,
        number = 3,
        color = infielderColor
    ),
    DisplayFielder(
        id = 4,
        displayName = "佐藤",
        position = Position.THIRD_BASEMAN,
        number = 4,
        color = infielderColor
    ),
    DisplayFielder(
        id = 5,
        displayName = "高橋",
        position = Position.RIGHT_FIELDER,
        number = 5,
        color = infielderColor
    ),
    DisplayFielder(
        id = 6,
        displayName = "伊藤",
        position = Position.SHORTSTOP,
        number = 6,
        color = infielderColor
    ),
    DisplayFielder(
        id = 7,
        displayName = "渡辺",
        position = Position.SECOND_BASEMAN,
        number = 7,
        color = infielderColor
    ),
    DisplayFielder(
        id = 8,
        displayName = "山本",
        position = Position.CATCHER,
        number = 8,
        color = catcherColor
    ),
    DisplayFielder(
        id = 9,
        displayName = "中村",
        position = Position.PITCHER,
        number = 9,
        color = pitcherColor
    ),
)

private val SAMPLE_TEAM_STANDING = TeamStanding(
    team = Team(
        id = 1,
        name = "A",
        league = League.L1
    ),
    rank = 1,
    wins = 10,
    losses = 5,
    draws = 2,
    gameBack = 0.0,
)

@Preview
@Composable
private fun BeforeGameContentPreview() {
    BeforeGameContent(
        date = LocalDate.of(2024, 4, 1),
        beforeGameInfo = BeforeGameInfo(
            homeTeam = SAMPLE_TEAM_STANDING,
            awayTeam = SAMPLE_TEAM_STANDING,
            homeStartingPlayers = SAMPLE_FIELDERS,
            awayStartingPlayers = SAMPLE_FIELDERS,
        ),
        modifier = Modifier
    )
}

@Preview
@Composable
private fun TeamInfoViewPreview() {
    TeamInfoView(
        teamStanding = SAMPLE_TEAM_STANDING,
        fielders = SAMPLE_FIELDERS,
        modifier = Modifier
    )
}

@Preview
@Composable
private fun OrderListPreview() {
    OrderList(
        fielders = SAMPLE_FIELDERS,
        modifier = Modifier
    )
}
