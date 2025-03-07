package com.example.penasim.model

import androidx.annotation.DrawableRes

data class TeamInfo(
    @DrawableRes val teamIcon: Int,
    val teamName: String,
    var wins: Int = 0,
    var losses: Int = 0,
    var draws: Int = 0,
    val league: Int,
    var rank: Int,
    var gameBack: Double = 0.0
)
