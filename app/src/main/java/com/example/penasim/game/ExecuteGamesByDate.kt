package com.example.penasim.game

import com.example.penasim.domain.GameInfo
import com.example.penasim.usecase.ExecuteGameUseCase
import com.example.penasim.usecase.GetGameSchedulesByDateUseCase
import com.example.penasim.usecase.GetTeamPlayersUseCase
import java.time.LocalDate
import javax.inject.Inject

class ExecuteGamesByDate @Inject constructor(
    private val executeGameUseCase: ExecuteGameUseCase,
    private val getTeamPlayersUseCase: GetTeamPlayersUseCase,
    private val getGameSchedulesByDateUseCase: GetGameSchedulesByDateUseCase
) {
    suspend fun execute(date: LocalDate): List<GameInfo> {
        val schedules = getGameSchedulesByDateUseCase.execute(date)
        return schedules.map { schedule ->
            val homeTeamPlayers = getTeamPlayersUseCase.execute(schedule.homeTeam.id)
            val awayTeamPlayers = getTeamPlayersUseCase.execute(schedule.awayTeam.id)

            val result = Match(schedule, homeTeamPlayers, awayTeamPlayers).play()
            executeGameUseCase.execute(
                fixtureId = schedule.fixture.id,
                homeScore = result.homeScore,
                awayScore = result.awayScore
            )
        }
    }
}