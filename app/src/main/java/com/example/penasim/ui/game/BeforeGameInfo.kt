package com.example.penasim.ui.game

import com.example.penasim.domain.TeamStanding
import com.example.penasim.ui.common.DisplayFielder

data class BeforeGameInfo(
    val homeTeam: TeamStanding = TeamStanding(),
    val awayTeam: TeamStanding = TeamStanding(),
    val homeStartingPlayers: List<DisplayFielder> = emptyList(),
    val awayStartingPlayers: List<DisplayFielder> = emptyList(),
)
