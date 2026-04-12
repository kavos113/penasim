package com.example.penasim.features.standing.ui.model

import com.example.penasim.const.icon
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.standing.domain.TeamStanding

data class RankingUiInfo(
  val league: League,
  val rank: Int,
  val teamIcon: Int,
  val gameBack: Double,
  val isMyTeam: Boolean,
)

fun TeamStanding.toRankingUiInfo(currentTeamId: Int): RankingUiInfo = RankingUiInfo(
  league = team.league,
  rank = rank,
  teamIcon = team.icon(),
  gameBack = gameBack,
  isMyTeam = team.id == currentTeamId
)
