package com.example.penasim.usecase

import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.repository.PitchingStatRepository
import javax.inject.Inject

class GetPitchingStatUseCase @Inject constructor(
    private val pitchingStatRepository: PitchingStatRepository
) {
    suspend fun executeByFixtureId(fixtureId: Int): List<PitchingStat> =
        pitchingStatRepository.getByFixtureId(fixtureId)

    suspend fun executeByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> =
        pitchingStatRepository.getByFixtureIds(fixtureIds)

    suspend fun executeByPlayerId(playerId: Int): List<PitchingStat> =
        pitchingStatRepository.getByPlayerId(playerId)

    suspend fun executeByPlayerIds(playerIds: List<Int>): List<PitchingStat> =
        pitchingStatRepository.getByPlayerIds(playerIds)
}
