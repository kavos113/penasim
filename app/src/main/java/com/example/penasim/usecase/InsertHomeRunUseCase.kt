package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.repository.HomeRunRepository

class InsertHomeRunUseCase(
    private val homeRunRepository: HomeRunRepository
) {
    suspend fun execute(homeRus: List<HomeRun>) {
        homeRunRepository.insertHomeRuns(homeRus)
    }
}