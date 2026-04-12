package com.example.penasim.features.schedule.usecase

import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.team.domain.Team
import java.time.LocalDate
import javax.inject.Inject

class GameScheduleUseCase @Inject constructor(
  private val gameScheduleResolver: GameScheduleResolver
) {
  suspend fun getByFixtureId(id: Int): GameSchedule {
    return gameScheduleResolver.getByFixtureId(id)
      ?: throw IllegalArgumentException("Game fixture with id $id not found")
  }

  suspend fun getByTeam(team: Team): List<GameSchedule> {
    return gameScheduleResolver.getByTeam(team)
  }

  suspend fun getByDate(date: LocalDate): List<GameSchedule> {
    return gameScheduleResolver.getByDate(date)
  }

  suspend fun getAll(): List<GameSchedule> {
    return gameScheduleResolver.getAll()
  }
}
