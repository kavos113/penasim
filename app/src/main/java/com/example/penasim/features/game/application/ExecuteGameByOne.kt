package com.example.penasim.features.game.application

import com.example.penasim.features.game.application.model.InGameSnapshot
import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.game.usecase.GameInfoUseCase
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.team.usecase.TeamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

class ExecuteGameByOne @Inject constructor(
  private val teamUseCase: TeamUseCase,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val gameInfoUseCase: GameInfoUseCase,
  private val simulationRunner: GameSimulationRunner,
  private val gameResultPersister: GameResultPersister
) {
  private var session: LiveGameSession? = null
  private lateinit var myTeam: Team
  private lateinit var date: LocalDate
  private var isFinished = false

  suspend fun start(myTeam: Team, date: LocalDate) {
    this.myTeam = myTeam
    this.date = date
    executeOtherGames()

    val schedule = gameScheduleUseCase
      .getByDate(date)
      .find { it.homeTeam == myTeam || it.awayTeam == myTeam }
    if (schedule == null) {
      println("today has no game for your team")
      session = null
      return
    }

    val homeTeamPlayers = teamUseCase.getTeamPlayers(schedule.homeTeam.id)
    val awayTeamPlayers = teamUseCase.getTeamPlayers(schedule.awayTeam.id)
    session = LiveGameSession(schedule, homeTeamPlayers, awayTeamPlayers)
    isFinished = false
  }

  fun next(): Pair<Boolean, InGameSnapshot> {
    val currentSession = session ?: throw IllegalStateException("Game session is not started")
    val (hasNext, snapshot) = currentSession.next()
    return hasNext to snapshot
  }

  suspend fun postFinishGame(): List<GameInfo> {
    if (isFinished || session == null) return emptyList()

    isFinished = true
    val result = requireNotNull(session).finish()
    gameResultPersister.persist(result)
    return gameInfoUseCase.getByDate(date)
  }

  private suspend fun executeOtherGames(): List<GameInfo> {
    val schedules = gameScheduleUseCase
      .getByDate(date)
      .filterNot { it.homeTeam == myTeam || it.awayTeam == myTeam }
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
