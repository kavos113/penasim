package com.example.penasim.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penasim.domain.InningScore
import com.example.penasim.ui.theme.hitColor
import com.example.penasim.ui.theme.onPrimaryLight
import com.example.penasim.ui.theme.playerBorderColor
import com.example.penasim.ui.theme.primaryLight

private const val MAX_INNINGS = 9

@Composable
internal fun InningScoresTable(
  homeTeamName: String,
  awayTeamName: String,
  homeInningScores: List<InningScore>,
  awayInningScores: List<InningScore>,
  modifier: Modifier = Modifier,
  inspectLastInning: Boolean = false
) {
  val isInspectHome = inspectLastInning && homeInningScores.size >= awayInningScores.size
  val isInspectAway = inspectLastInning && awayInningScores.size > homeInningScores.size

  Column(
    modifier = modifier
  ) {
    InningHeader(
      modifier = Modifier
        .fillMaxWidth()
    )
    InningScoreRow(
      teamName = awayTeamName,
      inningScores = awayInningScores,
      inspectLastInning = isInspectAway,
      modifier = Modifier
        .fillMaxWidth()
    )
    InningScoreRow(
      teamName = homeTeamName,
      inningScores = homeInningScores,
      inspectLastInning = isInspectHome,
      modifier = Modifier
        .fillMaxWidth()
    )
  }
}

@Composable
private fun InningScoreRow(
  teamName: String,
  inningScores: List<InningScore>,
  inspectLastInning: Boolean,
  modifier: Modifier = Modifier,
) {
  val inningStr = List(MAX_INNINGS) { i ->
    inningScores.map { it.score.toString() }.getOrElse(i) { "" }
  }
  Row {
    InningScoreItem(
      value = teamName,
      inspect = false,
      modifier = Modifier
        .width(48.dp)
        .height(32.dp)
    )
    inningStr.forEachIndexed { i, inningScore ->
      InningScoreItem(
        value = inningScore,
        inspect = inspectLastInning && i == inningScores.size - 1,
        modifier = Modifier
          .width(32.dp)
          .height(32.dp)
      )
    }
    InningScoreItem(
      value = inningScores.sumOf { it.score }.toString(),
      inspect = false,
      modifier = Modifier
        .width(36.dp)
        .height(32.dp)
    )
  }
}

@Composable
private fun InningScoreItem(
  value: String,
  inspect: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxSize()
      .background(if (inspect) hitColor else Color.Transparent)
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

private val SAMPLE_BLANK_HOME_INNING_SCORES = listOf(
  InningScore(fixtureId = 0, teamId = 1, inning = 1, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 2, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 3, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 4, score = 0),
)

private val SAMPLE_BLANK_AWAY_INNING_SCORES = listOf(
  InningScore(fixtureId = 0, teamId = 1, inning = 1, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 2, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 3, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 4, score = 0),
  InningScore(fixtureId = 0, teamId = 1, inning = 5, score = 1),
)

@Preview
@Composable
private fun InningScoresTablePreview() {
  InningScoresTable(
    homeTeamName = "A",
    awayTeamName = "B",
    homeInningScores = SAMPLE_HOME_INNING_SCORES,
    awayInningScores = SAMPLE_AWAY_INNING_SCORES,
    modifier = Modifier
      .fillMaxWidth(),
    inspectLastInning = false
  )
}

@Preview
@Composable
private fun InningScoresTableWithBlankPreview() {
  InningScoresTable(
    homeTeamName = "A",
    awayTeamName = "B",
    homeInningScores = SAMPLE_BLANK_HOME_INNING_SCORES,
    awayInningScores = SAMPLE_BLANK_AWAY_INNING_SCORES,
    modifier = Modifier.fillMaxWidth(),
    inspectLastInning = true
  )
}

@Preview
@Composable
private fun InningScoreRowPreview() {
  InningScoreRow(
    teamName = "A",
    inningScores = SAMPLE_HOME_INNING_SCORES,
    inspectLastInning = false,
    modifier = Modifier
  )
}

@Preview
@Composable
private fun InningScoreRowBlankPreview() {
  InningScoreRow(
    teamName = "A",
    inningScores = SAMPLE_BLANK_HOME_INNING_SCORES,
    inspectLastInning = true,
    modifier = Modifier
  )
}