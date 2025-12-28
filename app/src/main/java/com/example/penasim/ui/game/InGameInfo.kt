package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.Position
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.theme.outfielderColor
import java.time.LocalDate

data class InGameInfo(
  val date: LocalDate = Constants.START,
  val homeTeam: InGameTeamInfo = InGameTeamInfo(),
  val awayTeam: InGameTeamInfo = InGameTeamInfo(),
  val outCount: Int = 0,
  val firstBase: DisplayFielder? = null,
  val secondBase: DisplayFielder? = null,
  val thirdBase: DisplayFielder? = null
)

data class InGameTeamInfo(
  val inningScores: List<InningScore> = emptyList(),
  val players: List<DisplayFielder> = emptyList(),
  val activePlayerId: Int = 0,
  val activeNumber: Int? = null // activeな打順
) {
  val mainFielders: List<DisplayFielder>
    get() = players.filterNot { it.position == Position.BENCH }

  val subFielders: List<DisplayFielder>
    get() = players.filter { it.position == Position.BENCH }

  val activePlayer: DisplayFielder
    get() = players.find { it.id == activePlayerId } ?: DisplayFielder(
      id = activePlayerId,
      displayName = "Unknown Player",
      position = Position.OUTFIELDER,
      number = activeNumber ?: 1,
      color = outfielderColor
    )
}