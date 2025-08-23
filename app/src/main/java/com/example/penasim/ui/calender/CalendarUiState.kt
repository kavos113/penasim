package com.example.penasim.ui.calender

import com.example.penasim.const.DataSource
import com.example.penasim.const.DateConst
import java.time.LocalDate

data class CalendarUiState(
    val games: Map<LocalDate, List<GameUiInfo>> = emptyMap(),
    val rankings: List<RankingUiInfo> = DataSource.rankings,
    val currentDay: LocalDate = DateConst.START
)