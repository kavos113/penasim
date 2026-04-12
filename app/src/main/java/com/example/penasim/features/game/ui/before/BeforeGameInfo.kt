package com.example.penasim.features.game.ui.before

import com.example.penasim.const.Constants
import com.example.penasim.features.command.ui.model.DisplayFielder
import com.example.penasim.features.standing.domain.TeamStanding
import java.time.LocalDate

data class BeforeGameInfo(
  val date: LocalDate = Constants.START,
  val homeTeam: TeamStanding = TeamStanding(),
  val awayTeam: TeamStanding = TeamStanding(),
  val homeStartingPlayers: List<DisplayFielder> = emptyList(),
  val awayStartingPlayers: List<DisplayFielder> = emptyList(),
)
