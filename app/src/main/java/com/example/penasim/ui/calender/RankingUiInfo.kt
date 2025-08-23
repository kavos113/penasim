package com.example.penasim.ui.calender

import com.example.penasim.const.icon
import com.example.penasim.domain.League
import com.example.penasim.domain.TeamStanding

data class RankingUiInfo(
    val league: League,
    val rank: Int,
    val teamIcon: Int,
    val gameBack: Double,
)

fun TeamStanding.toRankingUiInfo(): RankingUiInfo = RankingUiInfo(
    league = team.league,
    rank = rank,
    teamIcon = team.icon(),
    gameBack = gameBack,
)