package com.example.penasim.features.player.ui.component

import androidx.compose.ui.graphics.Color
import com.example.penasim.features.player.domain.Player
import com.example.penasim.features.player.domain.PlayerPosition
import com.example.penasim.features.player.domain.TotalBattingStats
import com.example.penasim.features.player.domain.TotalPitchingStats
import com.example.penasim.core.designsystem.theme.aColor
import com.example.penasim.core.designsystem.theme.bColor
import com.example.penasim.core.designsystem.theme.cColor
import com.example.penasim.core.designsystem.theme.dColor
import com.example.penasim.core.designsystem.theme.eColor
import com.example.penasim.core.designsystem.theme.fColor
import com.example.penasim.core.designsystem.theme.gColor
import com.example.penasim.core.designsystem.theme.sColor

data class DisplayPlayerDetail(
  val player: Player,
  val positions: List<PlayerPosition>,
  val battingStats: TotalBattingStats,
  val pitchingStats: TotalPitchingStats,
  val color: Color
)

fun Int.statusAlphabet(): String = if (this >= 90) {
  "S"
} else if (this >= 80) {
  "A"
} else if (this >= 70) {
  "B"
} else if (this >= 60) {
  "C"
} else if (this >= 50) {
  "D"
} else if (this >= 40) {
  "E"
} else if (this >= 30) {
  "F"
} else {
  "G"
}

fun Int.statusColor(): Color = if (this >= 90) {
  sColor
} else if (this >= 80) {
  aColor
} else if (this >= 70) {
  bColor
} else if (this >= 60) {
  cColor
} else if (this >= 50) {
  dColor
} else if (this >= 40) {
  eColor
} else if (this >= 30) {
  fColor
} else {
  gColor
}
