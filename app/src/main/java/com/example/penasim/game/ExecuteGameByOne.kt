package com.example.penasim.game

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.Team
import com.example.penasim.domain.TransactionProvider
import com.example.penasim.usecase.BattingStatUseCase
import com.example.penasim.usecase.ExecuteGameUseCase
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.HomeRunUseCase
import com.example.penasim.usecase.InningScoreUseCase
import com.example.penasim.usecase.PitchingStatUseCase
import com.example.penasim.usecase.TeamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

// 一打席ずつ実行する用
class ExecuteGameByOne @Inject constructor(
  private val executeGameUseCase: ExecuteGameUseCase,
  private val teamUseCase: TeamUseCase,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val gameInfoUseCase: GameInfoUseCase,
  private val battingStatUseCase: BattingStatUseCase,
  private val pitchingStatUseCase: PitchingStatUseCase,
  private val inningScoreUseCase: InningScoreUseCase,
  private val homeRunUseCase: HomeRunUseCase,
  private val transactionProvider: TransactionProvider
) {
  private lateinit var match: Match
  private lateinit var myTeam: Team
  private lateinit var date: LocalDate

  private var isFinished = false

  suspend fun start(myTeam: Team, date: LocalDate) {
    this.myTeam = myTeam
    this.date = date

    executeOtherGame()

    val schedule = gameScheduleUseCase
      .getByDate(date)
      .find { it.homeTeam == myTeam || it.awayTeam == myTeam }
    if (schedule == null) {
      println("today has no game for your team")
      return
    }

    val homeTeamPlayers = teamUseCase.getTeamPlayers(schedule.homeTeam.id)
    val awayTeamPlayers = teamUseCase.getTeamPlayers(schedule.awayTeam.id)

    match = Match(schedule, homeTeamPlayers, awayTeamPlayers)
  }

  fun next(): Pair<Boolean, ScoreData> {
    if (!match.next()) {
      return Pair(false, match.scoreData())
    }
    return Pair(true, match.scoreData())
  }

  suspend fun postFinishGame(): List<GameInfo> {
    if (isFinished) return emptyList()

    isFinished = true
    match.postFinishGame()

    val result = match.result()

    transactionProvider.runInTransaction {
      inningScoreUseCase.insertAll(match.inningScores())
      battingStatUseCase.insertAll(match.battingStats())
      pitchingStatUseCase.insertAll(match.pitchingStats())
      homeRunUseCase.insert(match.homeRuns())

      executeGameUseCase.execute(
        fixtureId = result.fixtureId,
        homeScore = result.homeScore,
        awayScore = result.awayScore
      )
    }

    return gameInfoUseCase.getByDate(date)
  }

  private suspend fun executeOtherGame(): List<GameInfo> {
    val schedules = gameScheduleUseCase
      .getByDate(date)
      .filterNot { it.homeTeam == myTeam || it.awayTeam == myTeam }
    println("Executing games for date: $date, total schedules: ${schedules.size}")

    return supervisorScope {
      val deferredResults = schedules.map { schedule ->
        async(Dispatchers.Default) {
          val homeTeamPlayers = teamUseCase.getTeamPlayers(schedule.homeTeam.id)
          val awayTeamPlayers = teamUseCase.getTeamPlayers(schedule.awayTeam.id)

          val match = Match(schedule, homeTeamPlayers, awayTeamPlayers)
          match.play()

          val result = match.result()

          transactionProvider.runInTransaction {
            inningScoreUseCase.insertAll(match.inningScores())
            battingStatUseCase.insertAll(match.battingStats())
            pitchingStatUseCase.insertAll(match.pitchingStats())
            homeRunUseCase.insert(match.homeRuns())

            executeGameUseCase.execute(
              fixtureId = result.fixtureId,
              homeScore = result.homeScore,
              awayScore = result.awayScore
            )
          }
        }
      }

      deferredResults.awaitAll()
    }
  }
}