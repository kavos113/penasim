package com.example.penasim.domain

data class PitchingStat(
    val gameFixtureId: Int,
    val playerId: Int,
    val inningPitched: Float,
    val hit: Int,
    val run: Int,
    val earnedRun: Int,
    val walk: Int,
    val strikeOut: Int,
    val homeRun: Int,
    val win: Boolean,
    val lose: Boolean,
    val hold: Boolean,
    val save: Boolean,
)
