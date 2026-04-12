package com.example.penasim.features.game.ui.ingame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penasim.core.ui.model.DisplayFielder
import com.example.penasim.features.game.application.model.AtBatResultType
import com.example.penasim.features.game.application.model.InGameAtBatResult
import com.example.penasim.features.player.domain.Position
import com.example.penasim.core.designsystem.component.PressingButton
import com.example.penasim.core.designsystem.component.InningScoresTable
import com.example.penasim.features.player.ui.component.OrderPlayerItem
import com.example.penasim.features.player.ui.component.SimplePlayerItem
import com.example.penasim.core.designsystem.theme.blankColor
import com.example.penasim.core.designsystem.theme.catcherColor
import com.example.penasim.core.designsystem.theme.hitColor
import com.example.penasim.core.designsystem.theme.infielderColor
import com.example.penasim.core.designsystem.theme.outColor
import com.example.penasim.core.designsystem.theme.outfielderColor
import com.example.penasim.core.designsystem.theme.pitcherColor
import com.example.penasim.core.designsystem.theme.scoreColor
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object InGameDestination

@Composable
fun InGameScreen(
  modifier: Modifier = Modifier,
  onGameFinish: () -> Unit = { },
  viewModel: InGameViewModel,
  currentDay: LocalDate
) {
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(currentDay) {
    viewModel.setDate(currentDay)
  }

  InGameContent(
    inGameInfo = uiState,
    onClickNext = {
      if (viewModel.next()) {
        onGameFinish()
      }
    },
    onClickSkip = {
      viewModel.skip()
      onGameFinish()
    },
    onClickFast = {
      if (viewModel.next()) {
        onGameFinish()
      }
    },
    modifier = modifier
  )
}

@Composable
private fun InGameContent(
  inGameInfo: InGameInfo,
  modifier: Modifier = Modifier,
  onClickNext: () -> Unit = { },
  onClickFast: () -> Unit = { },
  onClickSkip: () -> Unit = { },
  onClickChange: () -> Unit = { }
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(2.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    InningScoresTable(
      homeTeamName = inGameInfo.homeTeam.name,
      awayTeamName = inGameInfo.awayTeam.name,
      homeInningScores = inGameInfo.homeTeam.inningScores,
      awayInningScores = inGameInfo.awayTeam.inningScores
    )
    PlayerInfoContent(
      homePlayers = inGameInfo.homeTeam.mainFielders,
      awayPlayers = inGameInfo.awayTeam.mainFielders,
      homeActiveNumber = inGameInfo.homeTeam.activeNumber,
      awayActiveNumber = inGameInfo.awayTeam.activeNumber,
      firstBase = inGameInfo.firstBase,
      secondBase = inGameInfo.secondBase,
      thirdBase = inGameInfo.thirdBase,
      outCount = inGameInfo.outCount,
      lastResult = inGameInfo.lastResult
    )
    FooterItems(
      homeActivePlayer = inGameInfo.homeTeam.activePlayer,
      awayActivePlayer = inGameInfo.awayTeam.activePlayer,
      onClickNext = onClickNext,
      onClickFast = onClickFast,
      onClickSkip = onClickSkip,
      onClickChange = onClickChange
    )
  }
}

@Composable
private fun PlayerInfoContent(
  homePlayers: List<DisplayFielder>,
  awayPlayers: List<DisplayFielder>,
  homeActiveNumber: Int?,
  awayActiveNumber: Int?,
  firstBase: DisplayFielder?,
  secondBase: DisplayFielder?,
  thirdBase: DisplayFielder?,
  outCount: Int,
  lastResult: InGameAtBatResult,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    OrderList(
      fielders = awayPlayers,
      activeNumber = awayActiveNumber,
      modifier = Modifier
        .weight(1f)
    )
    Spacer(
      modifier = Modifier.weight(0.5f)
    )
    BaseInfo(
      firstBase = firstBase,
      secondBase = secondBase,
      thirdBase = thirdBase,
      outCount = outCount,
      lastResult = lastResult,
      modifier = Modifier
        .weight(1f)
    )
    Spacer(
      modifier = Modifier.weight(0.5f)
    )
    OrderList(
      fielders = homePlayers,
      activeNumber = homeActiveNumber,
      modifier = Modifier
        .weight(1f)
    )
  }
}

@Composable
private fun BaseInfo(
  firstBase: DisplayFielder?,
  secondBase: DisplayFielder?,
  thirdBase: DisplayFielder?,
  outCount: Int,
  lastResult: InGameAtBatResult,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(100.dp),
      modifier = Modifier
        .padding(top = 60.dp)
        .align(Alignment.Center)
    ) {
      LastResultText(
        lastResult = lastResult,
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
      )
      Box(
        modifier = Modifier
          .height(100.dp)
      ) {
        TriangleLayout {
          if (firstBase != null) {
            SimplePlayerItem(
              displayName = firstBase.displayName,
              color = firstBase.color,
              modifier = Modifier
                  .layoutId(TrianglePosition.BottomRight)
                  .width(120.dp)
            )
          }
          if (secondBase != null) {
            SimplePlayerItem(
              displayName = secondBase.displayName,
              color = secondBase.color,
              modifier = Modifier
                  .layoutId(TrianglePosition.Top)
                  .width(120.dp)
            )
          }
          if (thirdBase != null) {
            SimplePlayerItem(
              displayName = thirdBase.displayName,
              color = thirdBase.color,
              modifier = Modifier
                  .layoutId(TrianglePosition.BottomLeft)
                  .width(120.dp)
            )
          }
        }
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "O",
          fontSize = 24.sp
        )
        val outColors = when (outCount) {
          0 -> listOf(blankColor, blankColor)
          1 -> listOf(outColor, blankColor)
          else -> listOf(outColor, outColor)
        }
        Row {
          repeat(outColors.size) { i ->
            Canvas(modifier = Modifier.size(24.dp)) {
              drawCircle(
                color = outColors[i],
                radius = size.minDimension / 2
              )
            }
          }
        }
      }
    }
  }
}

private enum class TrianglePosition {
  Top,
  BottomLeft,
  BottomRight
}

@Composable
private fun TriangleLayout(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Layout(
    content = content,
    modifier = modifier
  ) { measurables, constraints ->
    val placeable = measurables.map { it.layoutId as TrianglePosition to it.measure(constraints) }

    layout(constraints.maxWidth, constraints.maxHeight) {
      val width = constraints.maxWidth.toFloat()
      val height = constraints.maxHeight.toFloat()

      val positions = mapOf(
        TrianglePosition.Top to Offset(width / 2f, 0f),
        TrianglePosition.BottomLeft to Offset(0f, height),
        TrianglePosition.BottomRight to Offset(width, height)
      )

      placeable.forEach { (pos, component) ->
        val coord = positions[pos] ?: Offset.Zero
        component.placeRelative(
          x = (coord.x - component.width / 2).toInt(),
          y = (coord.y - component.height / 2).toInt()
        )
      }
    }
  }
}

// activeNumber: 1-9
@Composable
private fun OrderList(
  fielders: List<DisplayFielder>,
  activeNumber: Int?,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    repeat(fielders.size) {
      OrderPlayerItem(
        player = fielders[it],
        modifier = Modifier,
        isActive = it + 1 == activeNumber
      )
    }
  }
}

@Composable
private fun FooterItems(
  homeActivePlayer: DisplayFielder,
  awayActivePlayer: DisplayFielder,
  modifier: Modifier = Modifier,
  onClickNext: () -> Unit = { },
  onClickFast: () -> Unit = { },
  onClickSkip: () -> Unit = { },
  onClickChange: () -> Unit = { }
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .then(modifier)
  ) {
    Row {
      SimplePlayerItem(
        displayName = awayActivePlayer.displayName,
        color = awayActivePlayer.color,
        modifier = Modifier
          .weight(1f)
      )
      Spacer(
        modifier = Modifier
          .weight(0.5f)
      )
      SimplePlayerItem(
        displayName = homeActivePlayer.displayName,
        color = homeActivePlayer.color,
        modifier = Modifier
          .weight(1f)
      )
    }
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
    ) {
      Button(
        onClick = onClickNext
      ) {
        Text("次へ")
      }
      PressingButton(
        buttonText = "高速",
        intervalMs = 100,
        function = onClickFast
      )
      Button(
        onClick = onClickSkip
      ) {
        Text("skip")
      }
      Button(
        onClick = onClickChange
      ) {
        Text("交代")
      }
    }
  }
}

@Composable
private fun LastResultText(
  lastResult: InGameAtBatResult,
  modifier: Modifier = Modifier
) {
  if (lastResult.isScored) {
    Text(
      text = lastResult.type.toPlayDescription(),
      modifier = modifier
        .background(color = scoreColor)
        .padding(4.dp)
    )
  } else if (lastResult.isHit) {
    Text(
      text = lastResult.type.toPlayDescription(),
      modifier = modifier
        .background(color = hitColor)
        .padding(4.dp)
    )
  } else {
    Text(
      text = lastResult.type.toPlayDescription(),
      modifier = modifier
        .padding(4.dp)
    )
  }
}

@Composable
private fun SubstituteList(
  substitutes: List<DisplayFielder>,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
  ) {
    val (first, second) = substitutes.withIndex().partition { it.index % 2 == 0 }
    LazyColumn(
      modifier = Modifier
        .weight(1f)
    ) {
      items(first) { (_, fielder) ->
        SimplePlayerItem(
          displayName = fielder.displayName,
          color = fielder.color,
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        )
      }
    }
    LazyColumn(
      modifier = Modifier
        .weight(1f)
    ) {
      items(second) { (_, fielder) ->
        SimplePlayerItem(
          displayName = fielder.displayName,
          color = fielder.color,
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        )
      }
    }
  }
}

@Preview
@Composable
private fun LastResultTextPreview() {
  Column {
    LastResultText(
      lastResult = InGameAtBatResult(
        type = AtBatResultType.HOMERUN,
        isHit = true,
        isScored = true
      ),
      modifier = Modifier.padding(8.dp)
    )
    LastResultText(
      lastResult = InGameAtBatResult(
        type = AtBatResultType.SINGLE_HIT,
        isHit = true,
        isScored = false
      ),
      modifier = Modifier.padding(8.dp)
    )
    LastResultText(
      lastResult = InGameAtBatResult(
        type = AtBatResultType.OUT,
        isHit = false,
        isScored = false
      ),
      modifier = Modifier.padding(8.dp)
    )
  }
}

private val SAMPLE_ORDER = listOf(
  DisplayFielder(
    0,
    "Pitcher",
    Position.PITCHER,
    1,
    pitcherColor
  ),
  DisplayFielder(
    1,
    "Player 2",
    Position.CATCHER,
    2,
    catcherColor
  ),
  DisplayFielder(
    2,
    "Player 3",
    Position.FIRST_BASEMAN,
    3,
    infielderColor
  ),
  DisplayFielder(
    3,
    "Player 4",
    Position.SECOND_BASEMAN,
    4,
    infielderColor
  ),
  DisplayFielder(
    4,
    "Player 5",
    Position.THIRD_BASEMAN,
    5,
    infielderColor
  ),
  DisplayFielder(
    5,
    "Player 6",
    Position.SHORTSTOP,
    6,
    infielderColor
  ),
  DisplayFielder(
    6,
    "Player 7",
    Position.LEFT_FIELDER,
    7,
    infielderColor
  ),
  DisplayFielder(
    7,
    "Player 8",
    Position.CENTER_FIELDER,
    8,
    outfielderColor
  ),
  DisplayFielder(
    8,
    "Player 9",
    Position.RIGHT_FIELDER,
    9,
    outfielderColor
  ),
)

private val SAMPLE_SUBSTITUTES = listOf(
  DisplayFielder(
    9,
    "Substitute 1",
    Position.LEFT_FIELDER,
    10,
    infielderColor
  ),
  DisplayFielder(
    10,
    "Substitute 2",
    Position.CENTER_FIELDER,
    11,
    outfielderColor
  ),
  DisplayFielder(
    11,
    "Substitute 3",
    Position.RIGHT_FIELDER,
    12,
    outfielderColor
  ),
  DisplayFielder(
    12,
    "Substitute 4",
    Position.FIRST_BASEMAN,
    13,
    infielderColor
  ),
  DisplayFielder(
    13,
    "Substitute 5",
    Position.SECOND_BASEMAN,
    14,
    infielderColor
  ),
  DisplayFielder(
    14,
    "Substitute 6",
    Position.THIRD_BASEMAN,
    15,
    infielderColor
  ),
  DisplayFielder(
    15,
    "Substitute 7",
    Position.SHORTSTOP,
    16,
    infielderColor
  ),
  DisplayFielder(
    16,
    "Substitute 8",
    Position.CATCHER,
    17,
    catcherColor
  ),
  DisplayFielder(
    17,
    "Substitute 9",
    Position.PITCHER,
    18,
    pitcherColor
  ),
  DisplayFielder(
    18,
    "Substitute 10",
    Position.LEFT_FIELDER,
    19,
    infielderColor
  ),
  DisplayFielder(
    19,
    "Substitute 11",
    Position.CENTER_FIELDER,
    20,
    outfielderColor
  ),
  DisplayFielder(
    20,
    "Substitute 12",
    Position.RIGHT_FIELDER,
    21,
    outfielderColor
  ),
  DisplayFielder(
    21,
    "Substitute 13",
    Position.FIRST_BASEMAN,
    22,
    infielderColor
  ),
  DisplayFielder(
    22,
    "Substitute 14",
    Position.SECOND_BASEMAN,
    23,
    infielderColor
  ),
)

@Preview
@Composable
private fun SubstituteListPreview() {
  SubstituteList(
    substitutes = SAMPLE_SUBSTITUTES
  )
}

@Preview
@Composable
private fun OrderListPreview() {
  OrderList(
    fielders = SAMPLE_ORDER,
    activeNumber = 2
  )
}

@Preview
@Composable
private fun BaseInfoPreview() {
  BaseInfo(
    firstBase = DisplayFielder(
      6,
      "Player 7",
      Position.LEFT_FIELDER,
      7,
      infielderColor
    ),
    secondBase = DisplayFielder(
      7,
      "Player 8",
      Position.CENTER_FIELDER,
      8,
      outfielderColor
    ),
    thirdBase = DisplayFielder(
      8,
      "Player 9",
      Position.RIGHT_FIELDER,
      9,
      outfielderColor
    ),
    outCount = 1,
    lastResult = InGameAtBatResult(
      type = AtBatResultType.OUT,
      isHit = true,
      isScored = false
    )
  )
}

@Preview
@Composable
private fun PlayerInfoContentPreview() {
  PlayerInfoContent(
    homePlayers = SAMPLE_ORDER,
    awayPlayers = SAMPLE_ORDER,
    homeActiveNumber = 3,
    awayActiveNumber = null,
    firstBase = null,
    secondBase = DisplayFielder(
      7,
      "Player 8",
      Position.CENTER_FIELDER,
      8,
      outfielderColor
    ),
    thirdBase = DisplayFielder(
      8,
      "Player 9",
      Position.RIGHT_FIELDER,
      9,
      outfielderColor
    ),
    outCount = 1,
    lastResult = InGameAtBatResult(
      type = AtBatResultType.SINGLE_HIT,
      isHit = true,
      isScored = true
    )
  )
}

@Preview
@Composable
private fun FooterItemsPreview() {
  FooterItems(
    homeActivePlayer = DisplayFielder(
      8,
      "Player 9",
      Position.RIGHT_FIELDER,
      9,
      outfielderColor
    ),
    awayActivePlayer = DisplayFielder(
      0,
      "Pitcher",
      Position.PITCHER,
      1,
      pitcherColor
    ),
  )
}

@Preview
@Composable
private fun InGameContentPreview() {
  InGameContent(
    inGameInfo = InGameInfo(
      homeTeam = InGameTeamInfo(
        players = SAMPLE_ORDER,
        activePlayerId = 1,
        activeNumber = 2
      ),
      awayTeam = InGameTeamInfo(
        players = SAMPLE_ORDER
      ),
      firstBase = null,
      secondBase = DisplayFielder(
        7,
        "Player 8",
        Position.CENTER_FIELDER,
        8,
        outfielderColor
      ),
      thirdBase = DisplayFielder(
        8,
        "Player 9",
        Position.RIGHT_FIELDER,
        9,
        outfielderColor
      ),
      outCount = 1,
      lastResult = InGameAtBatResult(
        type = AtBatResultType.SINGLE_HIT,
        isHit = true,
        isScored = true
      )
    )
  )
}
