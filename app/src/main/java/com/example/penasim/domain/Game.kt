package com.example.penasim.domain

data class Game(
    val id: Int,
    val master: GameMaster,
    val homeScore: Int,
    val awayScore: Int,
)
