package com.example.penasim.domain

data class TeamStanding(
    val team: Team = Team(),
    val rank: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val gameBack: Double = 0.0,
)
