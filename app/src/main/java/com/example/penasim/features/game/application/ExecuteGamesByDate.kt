package com.example.penasim.features.game.application

import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.team.usecase.TeamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

class ExecuteGamesByDate @Inject constructor(
  private val teamUseCase: TeamUseCase,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val simulationRunner: GameSimulationRunner,
  private val gameResultPersister: GameResultPersister
) {
  suspend fun execute(date: LocalDate): List<GameInfo> {
    val schedules = gameScheduleUseCase.getByDate(date)
    println("Executing games for date: $date, total schedules: ${schedules.size}")

    return supervisorScope {
      val deferredResults = schedules.map { schedule ->
        async(Dispatchers.Default) {
          val homeTeamPlayers = teamUseCase.getTeamPlayers(schedule.homeTeam.id)
          val awayTeamPlayers = teamUseCase.getTeamPlayers(schedule.awayTeam.id)
          val result = simulationRunner.simulate(schedule, homeTeamPlayers, awayTeamPlayers)
          gameResultPersister.persist(result)
        }
      }
      deferredResults.awaitAll()
    }
  }
}
