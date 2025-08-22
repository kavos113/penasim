package com.example.penasim.domain

data class GameMaster(
    val id: Int,
    val date: Date,
    val numberOfGames: Int, // 節内で何番目か
    val homeTeam: Team,
    val awayTeam: Team,
)