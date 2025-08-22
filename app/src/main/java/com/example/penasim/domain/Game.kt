package com.example.penasim.domain

data class Game(
    val id: Int,
    val date: Schedule,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val isFinished: Boolean,
)
