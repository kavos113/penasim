package com.example.penasim.features.game.application

import com.example.penasim.features.command.domain.TeamPlayers
import com.example.penasim.features.game.application.model.SimulationResult
import com.example.penasim.features.game.engine.Match
import com.example.penasim.features.schedule.domain.GameSchedule
import javax.inject.Inject

class GameSimulationRunner @Inject constructor() {
  fun simulate(
    schedule: GameSchedule,
    homePlayers: TeamPlayers,
    awayPlayers: TeamPlayers
  ): SimulationResult {
    val match = Match(schedule, homePlayers, awayPlayers)
    match.play()
    return match.simulationResult()
  }
}
