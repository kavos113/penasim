package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import javax.inject.Inject

class GetHomeRunUseCase @Inject constructor(
    private val homeRunUseCase: HomeRunUseCase
) {
    suspend fun execute(fixtureId: Int): List<HomeRun> = homeRunUseCase.getByFixtureId(fixtureId)
}