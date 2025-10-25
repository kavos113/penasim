package com.example.penasim.usecase

import com.example.penasim.domain.PitchingStat
import javax.inject.Inject

class InsertPitchingStatUseCase @Inject constructor(
    private val pitchingStatUseCase: PitchingStatUseCase
) {
    suspend fun execute(item: List<PitchingStat>) {
        pitchingStatUseCase.insertAll(item)
    }
}
