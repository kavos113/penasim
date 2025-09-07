package com.example.penasim.domain

enum class Position {
    PITCHER,
    CATCHER,
    FIRST_BASEMAN,
    SECOND_BASEMAN,
    THIRD_BASEMAN,
    SHORTSTOP,
    OUTFIELDER,
    LEFT_FIELDER,
    CENTER_FIELDER,
    RIGHT_FIELDER,
    DH,
}

fun Position.toJa(): String {
    return when (this) {
        Position.PITCHER -> "投手"
        Position.CATCHER -> "捕手"
        Position.FIRST_BASEMAN -> "一塁手"
        Position.SECOND_BASEMAN -> "二塁手"
        Position.THIRD_BASEMAN -> "三塁手"
        Position.SHORTSTOP -> "遊撃手"
        Position.OUTFIELDER -> "外野手"
        Position.LEFT_FIELDER -> "左翼手"
        Position.CENTER_FIELDER -> "中堅手"
        Position.RIGHT_FIELDER -> "右翼手"
        Position.DH -> "指名打者"
    }
}

fun Position.toShortJa(): String {
    return when (this) {
        Position.PITCHER -> "投"
        Position.CATCHER -> "捕"
        Position.FIRST_BASEMAN -> "一"
        Position.SECOND_BASEMAN -> "二"
        Position.THIRD_BASEMAN -> "三"
        Position.SHORTSTOP -> "遊"
        Position.OUTFIELDER -> "外"
        Position.LEFT_FIELDER -> "左"
        Position.CENTER_FIELDER -> "中"
        Position.RIGHT_FIELDER -> "右"
        Position.DH -> "指"
    }
}

data class PlayerPosition(
    val playerId: Int,
    val position: Position,
    val defense: Int,
)
