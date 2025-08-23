package com.example.penasim.domain

data class GameInfo(
    val fixture: GameFixture,
    val result: GameResult? = null,

    val homeTeam: Team,
    val awayTeam: Team,
) {
    val isFinished: Boolean
        get() = result != null
}
