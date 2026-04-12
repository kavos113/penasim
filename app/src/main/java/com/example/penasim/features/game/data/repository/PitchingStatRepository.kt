package com.example.penasim.features.game.data.repository

import com.example.penasim.features.game.data.dao.PitchingStatDao
import com.example.penasim.features.game.data.mapper.toDomain
import com.example.penasim.features.game.data.mapper.toEntity
import com.example.penasim.features.game.domain.PitchingStat
import com.example.penasim.features.game.domain.repository.PitchingStatRepository
import javax.inject.Inject

class PitchingStatRepository @Inject constructor(
  private val dao: PitchingStatDao
) : PitchingStatRepository {
  override suspend fun getByFixtureId(fixtureId: Int): List<PitchingStat> =
    dao.getByFixtureId(fixtureId).map { it.toDomain() }

  override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> =
    dao.getByFixtureIds(fixtureIds).map { it.toDomain() }

  override suspend fun getByPlayerId(playerId: Int): List<PitchingStat> =
    dao.getByPlayerId(playerId).map { it.toDomain() }

  override suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStat> =
    dao.getByPlayerIds(playerIds).map { it.toDomain() }

  override suspend fun insertAll(items: List<PitchingStat>) {
    dao.insertAll(items.map { it.toEntity() })
  }

  override suspend fun deleteByFixtureId(fixtureId: Int) {
    dao.deleteByFixtureId(fixtureId)
  }
}
