package com.example.penasim.ui.common

import com.example.penasim.const.Constants
import com.example.penasim.const.icon
import com.example.penasim.domain.League
import com.example.penasim.domain.TeamStanding

data class RankingUiInfo(
  val league: League,
  val rank: Int,
  val teamIcon: Int,
  val gameBack: Double,
  val isMyTeam: Boolean,
)

fun TeamStanding.toRankingUiInfo(): RankingUiInfo = RankingUiInfo(
  league = team.league,
  rank = rank,
  teamIcon = team.icon(),
  gameBack = gameBack,
  isMyTeam = team.id == Constants.TEAM_ID
)