package com.example.penasim.usecase

import com.example.penasim.domain.BattingStat
import javax.inject.Inject

class GetBattingStatUseCase @Inject constructor(
    private val battingStatUseCase: BattingStatUseCase
) {
    suspend fun executeByFixtureId(fixtureId: Int): List<BattingStat> =
        battingStatUseCase.getByFixtureId(fixtureId)

    suspend fun executeByFixtureIds(fixtureIds: List<Int>): List<BattingStat> =
        battingStatUseCase.getByFixtureIds(fixtureIds)

    suspend fun executeByPlayerId(playerId: Int): List<BattingStat> =
        battingStatUseCase.getByPlayerId(playerId)

    suspend fun executeByPlayerIds(playerIds: List<Int>): List<BattingStat> =
        battingStatUseCase.getByPlayerIds(playerIds)
}
