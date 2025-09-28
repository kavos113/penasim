package com.example.penasim.domain

data class BattingStat(
    val gameFixtureId: Int,
    val playerId: Int,
    val atBat: Int,
    val hit: Int,
    val doubleHit: Int,
    val tripleHit: Int,
    val homeRun: Int,
    val walk: Int,
    val rbi: Int,
    val strikeOut: Int,
)
