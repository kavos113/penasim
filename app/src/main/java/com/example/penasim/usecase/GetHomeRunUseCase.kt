package com.example.penasim.usecase

import com.example.penasim.domain.repository.HomeRunRepository

class GetHomeRunUseCase(
    private val homeRunRepository: HomeRunRepository
) {
    suspend fun execute(fixtureId: Int) = homeRunRepository.getHomeRunsByFixtureId(fixtureId)
}