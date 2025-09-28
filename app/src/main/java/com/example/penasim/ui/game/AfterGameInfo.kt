package com.example.penasim.ui.game

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.TeamStanding

data class AfterGameInfo(
    val scores: List<InningScore>,
    val homePitcherResults: List<PitcherResult>,
    val awayPitcherResults: List<PitcherResult>,
    val homeFielderResults: List<FielderResult>,
    val awayFielderResults: List<FielderResult>,
    val rankings: List<TeamStanding>,
)
