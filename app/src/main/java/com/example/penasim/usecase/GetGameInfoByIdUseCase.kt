package com.example.penasim.usecase

import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository

class GetGameInfoByIdUseCase(
    private val gameFixtureRepository: GameFixtureRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(id: Int): GameInfo {

    }
}