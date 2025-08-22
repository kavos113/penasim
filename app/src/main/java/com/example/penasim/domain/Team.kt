package com.example.penasim.domain

data class Team(
    val id: Int,
    val name: String,
    val league: League,
)

enum class League {
    L1,
    L2;

    companion object {
        fun fromId(id: Int): League = when (id) {
            1 -> L1
            2 -> L2
            else -> throw IllegalArgumentException("Unknown league id: $id")
        }

        fun toId(league: League): Int = when (league) {
            L1 -> 1
            L2 -> 2
        }
    }
}
