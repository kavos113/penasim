package com.example.penasim.domain

data class Game(
    val id: Int,
    val master: GameMaster,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
)
