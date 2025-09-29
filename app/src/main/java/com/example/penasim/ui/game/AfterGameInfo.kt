package com.example.penasim.ui.game

import com.example.penasim.domain.InningScore
import com.example.penasim.ui.common.RankingUiInfo

data class AfterGameInfo(
    val homeScores: List<InningScore> = emptyList(),
    val awayScores: List<InningScore> = emptyList(),
    val homePitcherResults: List<PitcherResult> = emptyList(),
    val awayPitcherResults: List<PitcherResult> = emptyList(),
    val homeFielderResults: List<FielderResult> = emptyList(),
    val awayFielderResults: List<FielderResult> = emptyList(),
    val rankings: List<RankingUiInfo> = emptyList(),
)
