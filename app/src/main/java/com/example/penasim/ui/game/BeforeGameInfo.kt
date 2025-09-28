package com.example.penasim.ui.game

import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamStanding
import com.example.penasim.ui.common.DisplayFielder
import java.time.LocalDate

data class BeforeGameInfo(
    val date: LocalDate,
    val homeTeam: TeamStanding,
    val awayTeam: TeamStanding,
    val homeStartingPlayers: List<DisplayFielder>,
    val awayStartingPlayers: List<DisplayFielder>,
)
