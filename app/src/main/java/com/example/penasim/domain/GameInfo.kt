package com.example.penasim.domain

data class GameInfo(
    val fixture: GameFixture,
    val homeTeam: Team,
    val awayTeam: Team,
    val result: GameResult,
)
