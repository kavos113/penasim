package com.example.penasim.game

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.TransactionProvider
import com.example.penasim.usecase.ExecuteGameUseCase
import com.example.penasim.usecase.GetGameSchedulesByDateUseCase
import com.example.penasim.usecase.GetTeamPlayersUseCase
import com.example.penasim.usecase.InsertBattingStatUseCase
import com.example.penasim.usecase.InsertHomeRunUseCase
import com.example.penasim.usecase.InsertInningScoreUseCase
import com.example.penasim.usecase.InsertPitchingStatUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

class ExecuteGamesByDate @Inject constructor(
    private val executeGameUseCase: ExecuteGameUseCase,
    private val getTeamPlayersUseCase: GetTeamPlayersUseCase,
    private val getGameSchedulesByDateUseCase: GetGameSchedulesByDateUseCase,
    private val insertBattingStatUseCase: InsertBattingStatUseCase,
    private val insertPitchingStatUseCase: InsertPitchingStatUseCase,
    private val insertInningScoreUseCase: InsertInningScoreUseCase,
    private val insertHomeRunUseCase: InsertHomeRunUseCase,
    private val transactionProvider: TransactionProvider
) {
    suspend fun execute(date: LocalDate): List<GameInfo> {
        val schedules = getGameSchedulesByDateUseCase.execute(date)
        println("Executing games for date: $date, total schedules: ${schedules.size}")

        return supervisorScope {
            val deferredResults = schedules.map { schedule ->
                async(Dispatchers.Default) {
                    val homeTeamPlayers = getTeamPlayersUseCase.execute(schedule.homeTeam.id)
                    val awayTeamPlayers = getTeamPlayersUseCase.execute(schedule.awayTeam.id)

                    val match = Match(schedule, homeTeamPlayers, awayTeamPlayers)
                    match.play()

                    val result = match.result()

                    transactionProvider.runInTransaction {
                        insertInningScoreUseCase.execute(match.inningScores())
                        insertBattingStatUseCase.execute(match.battingStats())
                        insertPitchingStatUseCase.execute(match.pitchingStats())
                        insertHomeRunUseCase.execute(match.homeRuns())

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