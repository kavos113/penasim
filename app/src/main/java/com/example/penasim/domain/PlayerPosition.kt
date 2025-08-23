package com.example.penasim.domain

enum class Position {
    PITCHER,
    CATCHER,
    FIRST_BASEMAN,
    SECOND_BASEMAN,
    THIRD_BASEMAN,
    SHORTSTOP,
    OUTFIELDER,
}

data class PlayerPosition(
    val playerId: Int,
    val position: Position,
    val defense: Int,
)
