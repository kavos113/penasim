package com.example.penasim.ui.game

import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.RankingUiInfo

data class InGameInfo(
    val ranking: List<RankingUiInfo>,
    val homeFielders: List<DisplayFielder>,
    val awayFielders: List<DisplayFielder>,
    val homeActiveNumber: Int,
    val awayActiveNumber: Int,
    val outCount: Int = 0,
    val firstBase: DisplayFielder? = null,
    val secondBase: DisplayFielder? = null,
    val thirdBase: DisplayFielder? = null
)
