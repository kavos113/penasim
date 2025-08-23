package com.example.penasim.domain

data class TeamStanding(
    val team: Team,
    val rank: Int,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val gameBack: Double = 0.0,
)
