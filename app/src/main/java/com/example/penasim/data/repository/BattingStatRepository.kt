package com.example.penasim.data.repository

import com.example.penasim.data.dao.BattingStatDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.data.mapper.toEntity
import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.repository.BattingStatRepository
import javax.inject.Inject

class BattingStatRepository @Inject constructor(
    private val dao: BattingStatDao
) : BattingStatRepository {
    override suspend fun getByFixtureId(fixtureId: Int): List<BattingStat>
        = dao.getByFixtureId(fixtureId).map { it.toDomain() }

    override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStat>
        = dao.getByFixtureIds(fixtureIds).map { it.toDomain() }

    override suspend fun getByPlayerId(playerId: Int): List<BattingStat>
        = dao.getByPlayerId(playerId).map { it.toDomain() }

    override suspend fun getByPlayerIds(playerIds: List<Int>): List<BattingStat>
        = dao.getByPlayerIds(playerIds).map { it.toDomain() }

    override suspend fun insertAll(items: List<BattingStat>) {
        dao.insertAll(items.map { it.toEntity() })
    }

    override suspend fun deleteByFixtureId(fixtureId: Int) {
        dao.deleteByFixtureId(fixtureId)
    }
}
