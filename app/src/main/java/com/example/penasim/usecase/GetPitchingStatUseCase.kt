package com.example.penasim.usecase

import com.example.penasim.domain.PitchingStat
import javax.inject.Inject

class GetPitchingStatUseCase @Inject constructor(
    private val pitchingStatUseCase: PitchingStatUseCase
) {
    suspend fun executeByFixtureId(fixtureId: Int): List<PitchingStat> =
        pitchingStatUseCase.getByFixtureId(fixtureId)

    suspend fun executeByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> =
        pitchingStatUseCase.getByFixtureIds(fixtureIds)

    suspend fun executeByPlayerId(playerId: Int): List<PitchingStat> =
        pitchingStatUseCase.getByPlayerId(playerId)

    suspend fun executeByPlayerIds(playerIds: List<Int>): List<PitchingStat> =
        pitchingStatUseCase.getByPlayerIds(playerIds)
}
