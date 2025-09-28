package com.example.penasim.domain

data class Team(
    val id: Int = 0,
    val name: String = "",
    val league: League = League.L1,
)

enum class League {
    L1,
    L2;
}

fun League.toId(): Int = when (this) {
    League.L1 -> 0
    League.L2 -> 1
}

fun Int.toLeague(): League = when (this) {
    0 -> League.L1
    1 -> League.L2
    else -> throw IllegalArgumentException("Unknown league id: $this")
}