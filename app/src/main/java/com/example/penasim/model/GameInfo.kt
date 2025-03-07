package com.example.penasim.model

data class GameInfo(
    val day: Int,
    val numberOfGames: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeTeamScore: Int,
    val awayTeamScore: Int
)
