package com.example.penasim.features.game.application.model

import com.example.penasim.features.game.domain.BattingStat
import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.game.domain.HomeRun
import com.example.penasim.features.game.domain.InningScore
import com.example.penasim.features.game.domain.PitchingStat
import com.example.penasim.features.game.domain.Stat

data class SimulationResult(
  val gameResult: GameResult,
  val inningScores: List<InningScore>,
  val battingStats: List<BattingStat>,
  val pitchingStats: List<PitchingStat>,
  val homeRuns: List<HomeRun>,
  val stats: List<Stat>,
)
