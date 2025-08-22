package com.example.penasim.ui.calender

import com.example.penasim.source.DataSource

data class GameUiInfo(
    val day: Int,
    val homeTeamIcon: Int,
    val homeTeamScore: Int,
    val awayTeamIcon: Int,
    val awayTeamScore: Int,
    val isGameFinished: Boolean
)

data class RankingUiInfo(
    val league: Int,
    val rank: Int,
    val teamIcon: Int,
    val gameBack: Double,
)

data class CalendarUiState(
    val games: List<List<GameUiInfo>> = DataSource.games,
    val rankings: List<RankingUiInfo> = DataSource.rankings,
    val currentDay: Int = 0
)