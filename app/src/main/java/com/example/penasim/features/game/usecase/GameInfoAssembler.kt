package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.schedule.domain.GameSchedule
import javax.inject.Inject

class GameInfoAssembler @Inject constructor() {
  fun fromSchedule(schedule: GameSchedule, result: GameResult): GameInfo {
    return GameInfo(
      fixture = schedule.fixture,
      homeTeam = schedule.homeTeam,
      awayTeam = schedule.awayTeam,
      result = result
    )
  }

  fun fromSchedules(
    schedules: List<GameSchedule>,
    results: List<GameResult>
  ): List<GameInfo> {
    val schedulesByFixtureId = schedules.associateBy { it.fixture.id }

    return results.map { result ->
      val schedule = schedulesByFixtureId[result.fixtureId]
        ?: throw IllegalArgumentException("Fixture with id ${result.fixtureId} not found")
      fromSchedule(schedule, result)
    }
  }
}
