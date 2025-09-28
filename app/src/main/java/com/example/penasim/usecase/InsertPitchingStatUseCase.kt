package com.example.penasim.usecase

import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.repository.PitchingStatRepository
import javax.inject.Inject

class InsertPitchingStatUseCase @Inject constructor(
    private val pitchingStatRepository: PitchingStatRepository
) {
    suspend fun execute(item: List<PitchingStat>) {
        pitchingStatRepository.insertAll(item)
    }
}
