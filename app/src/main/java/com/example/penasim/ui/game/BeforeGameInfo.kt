package com.example.penasim.ui.game

import com.example.penasim.const.Constants
import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamStanding
import com.example.penasim.ui.common.DisplayFielder
import java.time.LocalDate

data class BeforeGameInfo(
    val homeTeam: TeamStanding = TeamStanding(),
    val awayTeam: TeamStanding = TeamStanding(),
    val homeStartingPlayers: List<DisplayFielder> = emptyList(),
    val awayStartingPlayers: List<DisplayFielder> = emptyList(),
)
