package com.example.penasim.features.game.application.model

import com.example.penasim.features.game.domain.InningScore
import com.example.penasim.features.game.engine.BatterState
import com.example.penasim.features.game.engine.PitcherState

data class InGameSnapshot(
  val scores: List<InningScore>,
  val outCount: Int = 0,
  val firstBasePlayerId: Int? = null,
  val secondBasePlayerId: Int? = null,
  val thirdBasePlayerId: Int? = null,
  val lastResult: InGameAtBatResult,
  val isHomeBatting: Boolean = false,
  val homeBatterState: BatterState,
  val awayBatterState: BatterState,
  val homePitcherState: PitcherState,
  val awayPitcherState: PitcherState,
)
