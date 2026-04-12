package com.example.penasim.domain

import java.util.Locale

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
) {
  val battingAverage: Double
    get() = if (atBat == 0) {
      0.0
    } else {
      hit.toDouble() / atBat
    }
  val onBasePercentage: Double
    get() = if (atBat + walk == 0) {
      0.0
    } else {
      (hit + walk).toDouble() / (atBat + walk)
    }
  val sluggingPercentage: Double
    get() {
      if (atBat == 0) {
        return 0.0
      }
      val totalBases = hit + doubleHit + (2 * tripleHit) + (3 * homeRun)
      return totalBases.toDouble() / atBat
    }
  val ops: Double
    get() = onBasePercentage + sluggingPercentage

  val battingAverageString: String
    get() = String.format(Locale.JAPAN, "%.3f", battingAverage).drop(1) // drop leading "0"

  val onBasePercentageString: String
    get() = String.format(Locale.JAPAN, "%.3f", onBasePercentage).drop(1) // drop leading "0"

  val sluggingPercentageString: String
    get() = String.format(Locale.JAPAN, "%.3f", sluggingPercentage).drop(1) // drop leading "0"

  val opsString: String
    get() = String.format(Locale.JAPAN, "%.3f", ops).drop(1) // drop leading "0"
}

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