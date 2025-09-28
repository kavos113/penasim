package com.example.penasim.domain.repository

import com.example.penasim.domain.PitchingStat

interface PitchingStatRepository {
    suspend fun getByFixtureId(fixtureId: Int): List<PitchingStat>
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStat>
    suspend fun getByPlayerId(playerId: Int): List<PitchingStat>
    suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStat>
    suspend fun insertAll(items: List<PitchingStat>)
    suspend fun deleteByFixtureId(fixtureId: Int)
}
