package com.example.penasim.domain

import java.util.Locale

data class TotalPitchingStats(
  val playerId: Int,
  val inningsPitched: Int = 0,
  val hits: Int = 0,
  val runs: Int = 0,
  val earnedRuns: Int = 0,
  val walks: Int = 0,
  val strikeOuts: Int = 0,
  val homeRuns: Int = 0,
  val wins: Int = 0,
  val losses: Int = 0,
  val holds: Int = 0,
  val saves: Int = 0,
) {
  val era: Double
    get() = if (inningsPitched == 0) {
      0.0
    } else {
      (earnedRuns.toDouble() * 9 / (inningsPitched / 3.0))
    }

  val eraStr: String
    get() {
      return if (inningsPitched == 0) {
        "-"
      } else {
        String.format(Locale.JAPAN, "%.2f", era)
      }
    }
}

fun List<PitchingStat>.toTotalPitchingStats(): TotalPitchingStats = TotalPitchingStats(
  playerId = this.firstOrNull()?.playerId ?: 0,
  inningsPitched = this.sumOf { it.inningPitched },
  hits = this.sumOf { it.hit },
  runs = this.sumOf { it.run },
  earnedRuns = this.sumOf { it.earnedRun },
  walks = this.sumOf { it.walk },
  strikeOuts = this.sumOf { it.strikeOut },
  homeRuns = this.sumOf { it.homeRun },
  wins = this.count { it.win },
  losses = this.count { it.lose },
  holds = this.count { it.hold },
  saves = this.count { it.save },
)