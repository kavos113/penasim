package com.example.penasim.usecase

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.repository.InningScoreRepository
import javax.inject.Inject

class InsertInningScoreUseCase @Inject constructor(
    private val inningScoreRepository: InningScoreRepository
) {
    suspend fun execute(item: List<InningScore>) {
        inningScoreRepository.insertAll(item)
    }
}
