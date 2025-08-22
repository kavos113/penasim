package com.example.penasim.domain

data class Team(
    val id: Int,
    val name: String,
    val league: League,
)

enum class League {
    L1,
    L2;
}

fun League.toId(): Int = when (this) {
    League.L1 -> 1
    League.L2 -> 2
}

fun Int.toLeague(): League = when (this) {
    1 -> League.L1
    2 -> League.L2
    else -> throw IllegalArgumentException("Unknown league id: $this")
}