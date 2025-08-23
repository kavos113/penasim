package com.example.penasim.usecase

import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository

class GetGameScheduleUseCase(
    private val gameFixtureRepository: GameFixtureRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(id: Int): GameSchedule {
        val fixture = gameFixtureRepository.getGameFixture(id)
            ?: throw IllegalArgumentException("Game fixture with id $id not found")

        val homeTeam = teamRepository.getTeam(fixture.homeTeamId)
            ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
        val awayTeam = teamRepository.getTeam(fixture.awayTeamId)
            ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")

        return GameSchedule(
            fixture = fixture,
            homeTeam = homeTeam,
            awayTeam = awayTeam
        )
    }
}