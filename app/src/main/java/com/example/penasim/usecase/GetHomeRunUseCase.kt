package com.example.penasim.usecase

import com.example.penasim.domain.repository.HomeRunRepository
import javax.inject.Inject

class GetHomeRunUseCase @Inject constructor(
    private val homeRunRepository: HomeRunRepository
) {
    suspend fun execute(fixtureId: Int) = homeRunRepository.getHomeRunsByFixtureId(fixtureId)
}