package com.example.penasim.features.home.ui.home

import com.example.penasim.const.Constants
import java.time.LocalDate

data class HomeUiState(
  val teamId: Int = 0,
  val currentDay: LocalDate = Constants.START,
  val rank: Int = 0,
  val isGameDay: Boolean = true,
)
