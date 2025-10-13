package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.repository.HomeRunRepository
import javax.inject.Inject

class InsertHomeRunUseCase @Inject constructor(
    private val homeRunRepository: HomeRunRepository
) {
    suspend fun execute(homeRus: List<HomeRun>) {
        homeRunRepository.insertHomeRuns(homeRus)
    }
}