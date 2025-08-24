package com.example.penasim.ui.command

import androidx.compose.ui.graphics.Color
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.ui.theme.aColor
import com.example.penasim.ui.theme.bColor
import com.example.penasim.ui.theme.cColor
import com.example.penasim.ui.theme.catcherColor
import com.example.penasim.ui.theme.dColor
import com.example.penasim.ui.theme.eColor
import com.example.penasim.ui.theme.fColor
import com.example.penasim.ui.theme.gColor
import com.example.penasim.ui.theme.infielderColor
import com.example.penasim.ui.theme.outfielderColor
import com.example.penasim.ui.theme.pitcherColor
import com.example.penasim.ui.theme.sColor

data class DisplayPlayerDetail(
    val player: Player,
    val positions: List<PlayerPosition>,
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