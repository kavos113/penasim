package com.example.penasim.domain

data class GameSchedule(
    val fixture: GameFixture,
    val homeTeam: Team,
    val awayTeam: Team,
)
