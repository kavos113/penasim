package com.example.penasim.ui.command

import androidx.compose.ui.graphics.Color
import com.example.penasim.domain.Position
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor

data class DisplayFielder(
    val displayName: String,
    val position: String,
    val number: Int,
    val isMain: Boolean,
    val color: Color
)

fun Position.color(): Color = when(this) {
    Position.PITCHER -> pitcherColor
    Position.CATCHER -> catcherColor
    Position.FIRST_BASEMAN -> infielderColor
    Position.SECOND_BASEMAN -> infielderColor
    Position.THIRD_BASEMAN -> infielderColor
    Position.SHORTSTOP -> infielderColor
    Position.OUTFIELDER -> outfielderColor
    Position.LEFT_FIELDER -> outfielderColor
    Position.CENTER_FIELDER -> outfielderColor
    Position.RIGHT_FIELDER -> outfielderColor
    Position.DH -> outfielderColor
}