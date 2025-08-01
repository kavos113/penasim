package com.example.penasim.model

data class GameInfo(
    val day: Int,
    val numberOfGames: Int, // 節内で何番目か
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeTeamScore: Int,
    val awayTeamScore: Int
)
