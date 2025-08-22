package com.example.penasim.domain

import java.time.LocalDate

data class GameInfo(
    val id: Int,
    val date: LocalDate,
    val numberOfGames: Int, // 節内で何番目か
    val homeTeam: Team,
    val awayTeam: Team
)
