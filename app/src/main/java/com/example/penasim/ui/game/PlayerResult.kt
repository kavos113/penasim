package com.example.penasim.ui.game

import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.PlayerInfo

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

fun PitchingStat.toPitcherResult(
    info: PlayerInfo,
    number: Int
): PitcherResult = PitcherResult(
    displayName = info.player.firstName,
    number = number,
    wins = info.pitchingStat.wins,
    losses = info.pitchingStat.losses,
    holds = info.pitchingStat.holds,
    saves = info.pitchingStat.saves,
    isWin = this.win,
    isLoss = this.lose,
    isHold = this.hold,
    isSave = this.save,
)

data class FielderResult(
    val displayName: String,
    val inning: Int,
    val numberOfHomeRuns: Int,
)