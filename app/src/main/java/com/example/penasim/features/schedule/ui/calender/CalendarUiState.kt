package com.example.penasim.ui.calender

import com.example.penasim.const.Constants
import com.example.penasim.const.DataSource
import com.example.penasim.ui.common.GameUiInfo
import com.example.penasim.ui.common.RankingUiInfo
import java.time.LocalDate

data class CalendarUiState(
  val games: Map<LocalDate, List<GameUiInfo>> = emptyMap(),
  val rankings: List<RankingUiInfo> = DataSource.rankings,
  val currentDay: LocalDate = Constants.START
)