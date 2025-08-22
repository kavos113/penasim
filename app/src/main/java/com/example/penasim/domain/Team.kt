package com.example.penasim.domain

data class Team(
    val id: Int,
    val name: String,
    val league: League,
)

enum class League {
    L1,
    L2,
}
