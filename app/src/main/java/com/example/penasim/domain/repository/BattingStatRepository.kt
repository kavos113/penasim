package com.example.penasim.domain.repository

import com.example.penasim.domain.BattingStat

interface BattingStatRepository {
    suspend fun getByFixtureId(fixtureId: Int): List<BattingStat>
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStat>
    suspend fun getByPlayerId(playerId: Int): List<BattingStat>
    suspend fun getByPlayerIds(playerIds: List<Int>): List<BattingStat>
    suspend fun insertAll(items: List<BattingStat>)
    suspend fun deleteByFixtureId(fixtureId: Int)
}
