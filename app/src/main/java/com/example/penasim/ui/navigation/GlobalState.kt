package com.example.penasim.ui.navigation

import com.example.penasim.const.Constants
import java.time.LocalDate

data class GlobalState(
    val currentDay: LocalDate = Constants.START,
    val teamId: Int = Constants.TEAM_ID
)