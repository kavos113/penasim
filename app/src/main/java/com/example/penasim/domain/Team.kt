package com.example.penasim.domain

data class Team(
    val id: Int,
    val name: String,
    val league: League,
)

enum class League {
    L1,
    L2;

    fun fromId(id: Int): League? {
        return when (id) {
            1 -> L1
            2 -> L2
            else -> null
        }
    }
}
