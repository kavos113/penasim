package com.example.penasim.game

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.TransactionProvider
import com.example.penasim.usecase.BattingStatUseCase
import com.example.penasim.usecase.ExecuteGameUseCase
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

class ExecuteGamesByDate @Inject constructor(
    private val executeGameUseCase: ExecuteGameUseCase,
    private val teamUseCase: TeamUseCase,
    private val gameScheduleUseCase: GameScheduleUseCase,
    private val battingStatUseCase: BattingStatUseCase,
    private val pitchingStatUseCase: PitchingStatUseCase,
    private val inningScoreUseCase: InningScoreUseCase,
    private val homeRunUseCase: HomeRunUseCase,
    private val transactionProvider: TransactionProvider
) {
    suspend fun execute(date: LocalDate): List<GameInfo> {
        val schedules = gameScheduleUseCase.getByDate(date)
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