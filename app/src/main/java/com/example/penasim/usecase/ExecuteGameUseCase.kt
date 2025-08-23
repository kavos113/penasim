package com.example.penasim.usecase

import com.example.penasim.domain.GameResult
import com.example.penasim.domain.repository.GameResultRepository

class ExecuteGameUseCase(
    private val gameResultRepository: GameResultRepository
) {
    suspend fun execute(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult
        = gameResultRepository.createGame(fixtureId, homeScore, awayScore) ?: throw IllegalArgumentException("this fixtureId is already used")
}