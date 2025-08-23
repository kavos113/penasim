package com.example.penasim.usecase

import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository

class GetGameSchedulesByTeamUseCase(
    private val gameFixtureRepository: GameFixtureRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(team: Team): List<GameSchedule> {
        val fixtures = gameFixtureRepository.getGameFixturesByTeam(team)

        val teams = fixtures.flatMap { listOf(it.homeTeamId, it.awayTeamId) }
            .distinct()
            .mapNotNull { teamRepository.getTeam(it) }

        return fixtures.map { fixture ->
            val homeTeam = teams.find { it.id == fixture.homeTeamId }
                ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
            val awayTeam = teams.find { it.id == fixture.awayTeamId }
                ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")
            GameSchedule(
                fixture = fixture,
                homeTeam = homeTeam,
                awayTeam = awayTeam
            )
        }
    }
}