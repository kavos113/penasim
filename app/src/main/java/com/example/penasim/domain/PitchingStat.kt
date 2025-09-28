package com.example.penasim.domain

data class PitchingStat(
    val gameFixtureId: Int,
    val playerId: Int,
    val inningPitched: Float = 0f,
    val hit: Int = 0,
    val run: Int = 0,
    val earnedRun: Int = 0,
    val walk: Int = 0,
    val strikeOut: Int = 0,
    val homeRun: Int = 0,
    val win: Boolean = false,
    val lose: Boolean = false,
    val hold: Boolean = false,
    val save: Boolean = false,
)
