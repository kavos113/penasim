package com.example.penasim.ui.calender

import com.example.penasim.const.icon
import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.GameSchedule

data class GameUiInfo(
    val homeTeamIcon: Int,
    val homeTeamScore: Int,
    val awayTeamIcon: Int,
    val awayTeamScore: Int,
    val isGameFinished: Boolean
)

fun GameInfo.toGameUiInfo(): GameUiInfo = GameUiInfo(
    homeTeamIcon = homeTeam.icon(),
    homeTeamScore = result.homeScore,
    awayTeamIcon = awayTeam.icon(),
    awayTeamScore = result.awayScore,
    isGameFinished = true
)

fun GameSchedule.toGameUiInfo(): GameUiInfo = GameUiInfo(
    homeTeamIcon = homeTeam.icon(),
    homeTeamScore = 0,
    awayTeamIcon = awayTeam.icon(),
    awayTeamScore = 0,
    isGameFinished = false
)

fun GameSchedule.toGameUiInfoWithResult(result: GameInfo): GameUiInfo = GameUiInfo(
    homeTeamIcon = homeTeam.icon(),
    homeTeamScore = result.result.homeScore,
    awayTeamIcon = awayTeam.icon(),
    awayTeamScore = result.result.awayScore,
    isGameFinished = true
)