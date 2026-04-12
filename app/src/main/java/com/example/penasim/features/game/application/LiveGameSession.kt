package com.example.penasim.features.game.application

import com.example.penasim.features.command.domain.TeamPlayers
import com.example.penasim.features.game.application.model.InGameSnapshot
import com.example.penasim.features.game.application.model.SimulationResult
import com.example.penasim.features.game.engine.Match
import com.example.penasim.features.schedule.domain.GameSchedule

class LiveGameSession(
  schedule: GameSchedule,
  homePlayers: TeamPlayers,
  awayPlayers: TeamPlayers
) {
  private val match = Match(schedule, homePlayers, awayPlayers)

  fun next(): Pair<Boolean, InGameSnapshot> {
    val hasNext = match.next()
    return hasNext to match.snapshot()
  }

  fun finish(): SimulationResult = match.simulationResult()
}
