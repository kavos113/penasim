package com.example.penasim.ui.game

import com.example.penasim.domain.HomeRun
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
): PitcherResult = PitcherResult(
  displayName = info.player.firstName,
  number = this.numberOfPitches,
  wins = info.pitchingStat.wins + if (this.win) 1 else 0,
  losses = info.pitchingStat.losses + if (this.lose) 1 else 0,
  holds = info.pitchingStat.holds + if (this.hold) 1 else 0,
  saves = info.pitchingStat.saves + if (this.save) 1 else 0,
  isWin = this.win,
  isLoss = this.lose,
  isHold = this.hold,
  isSave = this.save,
)

data class FielderResult(
  val displayName: String,
  val inning: Int,
  val numberOfHomeRuns: Int,
  val type: HomeRunType,
)

enum class HomeRunType {
  SOLO,
  TWO_RUN,
  THREE_RUN,
  GRAND_SLAM,
}

fun Int.toHomeRunType(): HomeRunType = when (this) {
  1 -> HomeRunType.SOLO
  2 -> HomeRunType.TWO_RUN
  3 -> HomeRunType.THREE_RUN
  4 -> HomeRunType.GRAND_SLAM
  else -> throw IllegalArgumentException("Invalid number of runners on base: $this")
}

fun HomeRunType.toStringJp(): String = when (this) {
  HomeRunType.SOLO -> "ソロ"
  HomeRunType.TWO_RUN -> "2ラン"
  HomeRunType.THREE_RUN -> "3ラン"
  HomeRunType.GRAND_SLAM -> "満塁"
}

fun HomeRun.toFielderResult(
  info: PlayerInfo,
  indexInGame: Int,
): FielderResult = FielderResult(
  displayName = info.player.firstName,
  inning = this.inning,
  numberOfHomeRuns = info.battingStat.homeRun + indexInGame + 1,
  type = this.count.toHomeRunType()
)