package com.example.penasim.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import com.example.penasim.features.player.domain.Position

fun Position.color(): Color = when (this) {
  Position.CATCHER -> catcherColor
  Position.FIRST_BASEMAN,
  Position.SECOND_BASEMAN,
  Position.THIRD_BASEMAN,
  Position.SHORTSTOP -> infielderColor
  Position.DH,
  Position.LEFT_FIELDER,
  Position.CENTER_FIELDER,
  Position.RIGHT_FIELDER,
  Position.OUTFIELDER -> outfielderColor
  Position.PITCHER -> pitcherColor
  Position.BENCH,
  Position.SUBSTITUTE -> lightWhiteColor
}
