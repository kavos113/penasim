package com.example.penasim.usecase

import com.example.penasim.domain.InningScore
import javax.inject.Inject

class InsertInningScoreUseCase @Inject constructor(
    private val inningScoreUseCase: InningScoreUseCase
) {
    suspend fun execute(item: List<InningScore>) {
        inningScoreUseCase.insertAll(item)
    }
}
