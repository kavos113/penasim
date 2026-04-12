package com.example.penasim.core.navigation

import java.time.LocalDate

data class GlobalState(
  val currentDay: LocalDate,
  val teamId: Int
)
