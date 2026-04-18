package com.example.penasim

import androidx.compose.ui.graphics.Color
import com.example.penasim.core.designsystem.theme.PenasimTheme
import com.example.penasim.core.ui.model.DisplayFielder
import com.example.penasim.features.game.domain.InningScore
import com.example.penasim.features.game.ui.common.FielderResult
import com.example.penasim.features.game.ui.common.HomeRunType
import com.example.penasim.features.game.ui.common.PitcherResult
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.schedule.ui.model.GameUiInfo
import com.example.penasim.features.standing.domain.TeamStanding
import com.example.penasim.features.standing.ui.model.RankingUiInfo
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import java.time.LocalDate

fun sampleTeamStanding(teamId: Int, rank: Int): TeamStanding = TeamStanding(
  team = Team(teamId, "Team $teamId", if (teamId % 2 == 0) League.L1 else League.L2),
  rank = rank,
  wins = 10 - rank,
  losses = rank,
  draws = 0,
  gameBack = (rank - 1) * 1.5
)

fun sampleDisplayFielders(teamOffset: Int = 0): List<DisplayFielder> = listOf(
  DisplayFielder(teamOffset + 1, "Pitcher", Position.PITCHER, 1, Color.Red),
  DisplayFielder(teamOffset + 2, "Catcher", Position.CATCHER, 2, Color.Blue),
  DisplayFielder(teamOffset + 3, "First", Position.FIRST_BASEMAN, 3, Color.Green),
  DisplayFielder(teamOffset + 4, "Second", Position.SECOND_BASEMAN, 4, Color.Cyan),
  DisplayFielder(teamOffset + 5, "Third", Position.THIRD_BASEMAN, 5, Color.Magenta),
  DisplayFielder(teamOffset + 6, "Short", Position.SHORTSTOP, 6, Color.Yellow),
  DisplayFielder(teamOffset + 7, "Left", Position.LEFT_FIELDER, 7, Color.Gray),
  DisplayFielder(teamOffset + 8, "Center", Position.CENTER_FIELDER, 8, Color.DarkGray),
  DisplayFielder(teamOffset + 9, "Right", Position.RIGHT_FIELDER, 9, Color.LightGray),
)

fun sampleInningScores(teamId: Int): List<InningScore> = (1..3).map { inning ->
  InningScore(fixtureId = 1, teamId = teamId, inning = inning, score = inning - 1)
}

fun samplePitcherResults(): List<PitcherResult> = listOf(
  PitcherResult(
    displayName = "Ace",
    number = 1,
    wins = 3,
    losses = 1,
    holds = 0,
    saves = 0,
    isWin = true,
    isLoss = false,
    isHold = false,
    isSave = false
  )
)

fun sampleFielderResults(): List<FielderResult> = listOf(
  FielderResult(
    displayName = "Slugger",
    inning = 5,
    numberOfHomeRuns = 7,
    type = HomeRunType.TWO_RUN
  )
)

fun sampleRankings(): List<RankingUiInfo> = listOf(
  RankingUiInfo(league = League.L1, rank = 1, teamIcon = R.drawable.team1_icon, gameBack = 0.0, isMyTeam = true),
  RankingUiInfo(league = League.L1, rank = 2, teamIcon = R.drawable.team2_icon, gameBack = 1.5, isMyTeam = false),
)

fun sampleGames(date: LocalDate = LocalDate.of(2025, 3, 28)): Map<LocalDate, List<GameUiInfo>> = mapOf(
  date to listOf(
    GameUiInfo(
      homeTeamIcon = R.drawable.team1_icon,
      homeTeamScore = 3,
      awayTeamIcon = R.drawable.team2_icon,
      awayTeamScore = 1,
      isGameFinished = true
    )
  )
)

@androidx.compose.runtime.Composable
fun TestTheme(content: @androidx.compose.runtime.Composable () -> Unit) {
  PenasimTheme(content = content)
}
