package com.example.penasim.game

import com.example.penasim.domain.GameInfo
import com.example.penasim.usecase.ExecuteGameUseCase
import com.example.penasim.usecase.GetGameSchedulesByDateUseCase
import com.example.penasim.usecase.GetTeamPlayersUseCase
import com.example.penasim.usecase.InsertBattingStatUseCase
import com.example.penasim.usecase.InsertHomeRunUseCase
import com.example.penasim.usecase.InsertInningScoreUseCase
import com.example.penasim.usecase.InsertPitchingStatUseCase
import java.time.LocalDate
import javax.inject.Inject

class ExecuteGamesByDate @Inject constructor(
    private val executeGameUseCase: ExecuteGameUseCase,
    private val getTeamPlayersUseCase: GetTeamPlayersUseCase,
    private val getGameSchedulesByDateUseCase: GetGameSchedulesByDateUseCase,
    private val insertBattingStatUseCase: InsertBattingStatUseCase,
    private val insertPitchingStatUseCase: InsertPitchingStatUseCase,
    private val insertInningScoreUseCase: InsertInningScoreUseCase,
    private val insertHomeRunUseCase: InsertHomeRunUseCase
) {
    suspend fun execute(date: LocalDate): List<GameInfo> {
        val schedules = getGameSchedulesByDateUseCase.execute(date)
        println("Executing games for date: $date, total schedules: ${schedules.size}")
        return schedules.map { schedule ->
            val homeTeamPlayers = getTeamPlayersUseCase.execute(schedule.homeTeam.id)
            val awayTeamPlayers = getTeamPlayersUseCase.execute(schedule.awayTeam.id)

            val match = Match(schedule, homeTeamPlayers, awayTeamPlayers)
            match.play()

            val result = match.result()

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