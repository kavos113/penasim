package com.example.penasim.usecase

import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.repository.BattingStatRepository
import javax.inject.Inject

class GetBattingStatUseCase @Inject constructor(
    private val battingStatRepository: BattingStatRepository
) {
    suspend fun executeByFixtureId(fixtureId: Int): List<BattingStat> =
        battingStatRepository.getByFixtureId(fixtureId)

    suspend fun executeByFixtureIds(fixtureIds: List<Int>): List<BattingStat> =
        battingStatRepository.getByFixtureIds(fixtureIds)

    suspend fun executeByPlayerId(playerId: Int): List<BattingStat> =
        battingStatRepository.getByPlayerId(playerId)

    suspend fun executeByPlayerIds(playerIds: List<Int>): List<BattingStat> =
        battingStatRepository.getByPlayerIds(playerIds)
}
