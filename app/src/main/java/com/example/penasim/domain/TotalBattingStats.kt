package com.example.penasim.domain

data class TotalBattingStats(
    val playerId: Int,
    val atBat: Int = 0,
    val hit: Int = 0,
    val doubleHit: Int = 0,
    val tripleHit: Int = 0,
    val homeRun: Int = 0,
    val walk: Int = 0,
    val rbi: Int = 0,
    val strikeOut: Int = 0,
)

fun List<BattingStat>.toTotalBattingStats(): TotalBattingStats = TotalBattingStats(
    playerId = this.firstOrNull()?.playerId ?: 0,
    atBat = this.sumOf { it.atBat },
    hit = this.sumOf { it.hit },
    doubleHit = this.sumOf { it.doubleHit },
    tripleHit = this.sumOf { it.tripleHit },
    homeRun = this.sumOf { it.homeRun },
    walk = this.sumOf { it.walk },
    rbi = this.sumOf { it.rbi },
    strikeOut = this.sumOf { it.strikeOut },
)