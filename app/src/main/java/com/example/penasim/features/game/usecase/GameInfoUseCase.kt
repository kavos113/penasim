package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.game.domain.repository.GameResultRepository
import com.example.penasim.features.schedule.usecase.GameScheduleResolver
import java.time.LocalDate
import javax.inject.Inject

class GameInfoUseCase @Inject constructor(
  private val gameResultRepository: GameResultRepository,
  private val gameScheduleResolver: GameScheduleResolver,
  private val gameInfoAssembler: GameInfoAssembler
) {
  suspend fun getById(id: Int): GameInfo {
    val schedule = gameScheduleResolver.getByFixtureId(id)
      ?: throw IllegalArgumentException("Game fixture with id $id not found")
    val gameResult = gameResultRepository.getGameByFixtureId(id)
      ?: throw IllegalArgumentException("Game result for fixture with id $id not found")

    return gameInfoAssembler.fromSchedule(schedule, gameResult)
  }

  suspend fun getAll(): List<GameInfo> {
    val schedules = gameScheduleResolver.getAll()
    val results = gameResultRepository.getGamesByFixtureIds(
      schedules.map { it.fixture.id }
    )

    return gameInfoAssembler.fromSchedules(schedules, results)
  }

  suspend fun getByDate(date: LocalDate): List<GameInfo> {
    val schedules = gameScheduleResolver.getByDate(date)
    val gameResults = gameResultRepository.getGamesByFixtureIds(
      schedules.map { it.fixture.id }
    )

    return gameInfoAssembler.fromSchedules(schedules, gameResults)
  }

  suspend fun getByTeam(team: Team): List<GameInfo> {
    val schedules = gameScheduleResolver.getByTeam(team)
    val results = gameResultRepository.getGamesByFixtureIds(
      schedules.map { it.fixture.id }
    )

    return gameInfoAssembler.fromSchedules(schedules, results)
  }
}
