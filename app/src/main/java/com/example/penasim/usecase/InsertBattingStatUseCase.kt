package com.example.penasim.usecase

import com.example.penasim.domain.BattingStat
import javax.inject.Inject

class InsertBattingStatUseCase @Inject constructor(
    private val battingStatUseCase: BattingStatUseCase
) {
    suspend fun execute(item: List<BattingStat>) {
        battingStatUseCase.insertAll(item)
    }
}
