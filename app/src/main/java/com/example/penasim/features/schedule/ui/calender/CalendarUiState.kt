package com.example.penasim.features.schedule.ui.calender

import com.example.penasim.const.Constants
import com.example.penasim.const.DataSource
import com.example.penasim.features.schedule.ui.model.GameUiInfo
import com.example.penasim.features.standing.ui.model.RankingUiInfo
import java.time.LocalDate

data class CalendarUiState(
  val games: Map<LocalDate, List<GameUiInfo>> = emptyMap(),
  val rankings: List<RankingUiInfo> = DataSource.rankings,
  val currentDay: LocalDate = Constants.START
)