package com.example.penasim.ui.game

data class PitcherResult(
    val displayName: String,
    val number: Int,
    val wins: Int,
    val losses: Int,
    val holds: Int,
    val saves: Int,
    val isWin: Boolean,
    val isLoss: Boolean,
    val isHold: Boolean,
    val isSave: Boolean,
)

data class FielderResult(
    val displayName: String,
    val inning: Int,
    val numberOfHomeRuns: Int,
)