package com.example.penasim.usecase

import com.example.penasim.domain.GameResult
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import java.time.LocalDate

// TODO: remove random score generation
class ExecuteGamesByDateUseCase(
    private val gameResultRepository: GameResultRepository,
    private val gameFixtureRepository: GameFixtureRepository
) {
    suspend fun execute(date: LocalDate): List<GameResult> {
        val fixtures = gameFixtureRepository.getGameFixturesByDate(date)
        return fixtures.mapNotNull { fixture ->
            gameResultRepository.createGame(
                fixtureId = fixture.id,
                homeScore = (0..10).random(),
                awayScore = (0..10).random()
            )
        }
    }
}