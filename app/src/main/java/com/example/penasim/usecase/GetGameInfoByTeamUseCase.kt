package com.example.penasim.usecase

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import javax.inject.Inject

class GetGameInfoByTeamUseCase @Inject constructor(
    private val gameFixtureRepository: GameFixtureRepository,
    private val gameResultRepository: GameResultRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(team: Team): List<GameInfo> {
        val fixtures = gameFixtureRepository.getGameFixturesByTeam(team)

        val teams = fixtures.flatMap { listOf(it.homeTeamId, it.awayTeamId) }
            .distinct()
            .mapNotNull { teamRepository.getTeam(it) }

        val results = gameResultRepository.getGamesByFixtureIds(
            fixtures.map { it.id }
        )

        return results.map { gameResult ->
            val fixture = fixtures.find { it.id == gameResult.fixtureId }
                ?: throw IllegalArgumentException("Fixture with id ${gameResult.fixtureId} not found")

            val homeTeam = teams.find { it.id == fixture.homeTeamId }
                ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
            val awayTeam = teams.find { it.id == fixture.awayTeamId }
                ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")

            GameInfo(
                fixture = fixture,
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                result = gameResult
            )
        }
    }
}