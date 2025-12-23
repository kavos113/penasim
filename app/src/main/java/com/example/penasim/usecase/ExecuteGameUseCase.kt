package com.example.penasim.usecase

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import javax.inject.Inject

// game情報を格納
class ExecuteGameUseCase @Inject constructor(
    private val gameResultRepository: GameResultRepository,
    private val gameFixtureRepository: GameFixtureRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(fixtureId: Int, homeScore: Int, awayScore: Int): GameInfo {
        val result = gameResultRepository.createGame(fixtureId, homeScore, awayScore)
            ?: throw IllegalArgumentException("this fixtureId is already used")

        val fixture = gameFixtureRepository.getGameFixture(fixtureId)
            ?: throw IllegalArgumentException("no fixture for id $fixtureId")

        val homeTeam = teamRepository.getTeam(fixture.homeTeamId)
            ?: throw IllegalArgumentException("no team for id ${fixture.homeTeamId}")

        val awayTeam = teamRepository.getTeam(fixture.awayTeamId)
            ?: throw IllegalArgumentException("no team for id ${fixture.awayTeamId}")

        return GameInfo(
            fixture = fixture,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            result = result
        )
    }

}