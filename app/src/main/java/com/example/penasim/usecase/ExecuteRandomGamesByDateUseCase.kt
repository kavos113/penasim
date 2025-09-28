package com.example.penasim.usecase

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import java.time.LocalDate
import javax.inject.Inject

// TODO: remove random score generation
class ExecuteRandomGamesByDateUseCase @Inject constructor(
    private val gameResultRepository: GameResultRepository,
    private val gameFixtureRepository: GameFixtureRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(date: LocalDate): List<GameInfo> {
        val fixtures = gameFixtureRepository.getGameFixturesByDate(date)
        val results = fixtures.mapNotNull { fixture ->
            gameResultRepository.createGame(
                fixtureId = fixture.id,
                homeScore = (0..10).random(),
                awayScore = (0..10).random()
            )
        }

        return fixtures.map { fixture ->
            val homeTeam = teamRepository.getTeam(fixture.homeTeamId)
                ?: throw IllegalArgumentException("no team for id ${fixture.homeTeamId}")
            val awayTeam = teamRepository.getTeam(fixture.awayTeamId)
                ?: throw IllegalArgumentException("no team for id ${fixture.awayTeamId}")
            val result = results.find { it.fixtureId == fixture.id }
                ?: throw IllegalArgumentException("no result for fixture id ${fixture.id}")

            GameInfo(
                fixture = fixture,
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                result = result
            )
        }
    }
}