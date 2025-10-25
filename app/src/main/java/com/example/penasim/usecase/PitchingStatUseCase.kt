package com.example.penasim.usecase

import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.repository.PitchingStatRepository
import javax.inject.Inject

class PitchingStatUseCase @Inject constructor(
    private val repository: PitchingStatRepository
) {
    suspend fun getByFixtureId(fixtureId: Int): List<PitchingStat> = repository.getByFixtureId(fixtureId)
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> = repository.getByFixtureIds(fixtureIds)
    suspend fun getByPlayerId(playerId: Int): List<PitchingStat> = repository.getByPlayerId(playerId)
    suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStat> = repository.getByPlayerIds(playerIds)

    suspend fun insertAll(items: List<PitchingStat>) {
        if (items.isEmpty()) return
        repository.insertAll(items)
    }

    suspend fun deleteByFixtureId(fixtureId: Int) = repository.deleteByFixtureId(fixtureId)
}
