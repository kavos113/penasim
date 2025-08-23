package com.example.penasim.usecase

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository

class GetGameInfoUseCase(
    private val gameFixtureRepository: GameFixtureRepository,
    private val gameResultRepository: GameResultRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(id: Int): GameInfo {
        val fixture = gameFixtureRepository.getGameFixture(id)
            ?: throw IllegalArgumentException("Game fixture with id $id not found")

        val homeTeam = teamRepository.getTeam(fixture.homeTeamId)
            ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")

        val awayTeam = teamRepository.getTeam(fixture.awayTeamId)
            ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")

        val gameResult = gameResultRepository.getGameByFixtureId(id)
            ?: throw IllegalArgumentException("Game result for fixture with id $id not found")

        return GameInfo(
            fixture = fixture,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            result = gameResult
        )
    }
}