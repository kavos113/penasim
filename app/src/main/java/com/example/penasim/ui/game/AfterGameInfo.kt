package com.example.penasim.ui.game

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.TeamStanding

data class AfterGameInfo(
    val scores: List<InningScore> = emptyList(),
    val homePitcherResults: List<PitcherResult> = emptyList(),
    val awayPitcherResults: List<PitcherResult> = emptyList(),
    val homeFielderResults: List<FielderResult> = emptyList(),
    val awayFielderResults: List<FielderResult> = emptyList(),
    val rankings: List<TeamStanding> = emptyList(),
)
