package com.example.penasim.usecase

import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.repository.BattingStatRepository
import javax.inject.Inject

class InsertBattingStatUseCase @Inject constructor(
    private val battingStatRepository: BattingStatRepository
) {
    suspend fun execute(item: List<BattingStat>) {
        battingStatRepository.insertAll(item)
    }
}
