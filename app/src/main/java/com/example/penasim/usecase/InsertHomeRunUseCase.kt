package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import javax.inject.Inject

class InsertHomeRunUseCase @Inject constructor(
    private val homeRunUseCase: HomeRunUseCase
) {
    suspend fun execute(homeRus: List<HomeRun>) {
        homeRunUseCase.insert(homeRus)
    }
}