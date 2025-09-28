package com.example.penasim.ui.home

import com.example.penasim.const.DateConst
import java.time.LocalDate

data class HomeUiState(
    val teamId: Int = 0,
    val currentDay: LocalDate = DateConst.START,
    val rank: Int = 0,
)