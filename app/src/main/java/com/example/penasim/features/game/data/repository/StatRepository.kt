package com.example.penasim.features.game.data.repository

import com.example.penasim.features.game.data.dao.StatDao
import com.example.penasim.features.game.data.mapper.toDomain
import com.example.penasim.features.game.data.mapper.toEntity
import com.example.penasim.features.game.domain.Stat
import com.example.penasim.features.game.domain.repository.StatRepository
import javax.inject.Inject

class StatRepository @Inject constructor(
  private val dao: StatDao
) : StatRepository {
  override suspend fun getByFixtureId(fixtureId: Int): List<Stat> =
    dao.getByFixtureId(fixtureId).map { it.toDomain() }

  override suspend fun getByBatterId(batterId: Int): List<Stat> =
    dao.getByBatterId(batterId).map { it.toDomain() }

  override suspend fun getByPitcherId(pitcherId: Int): List<Stat> =
    dao.getByPitcherId(pitcherId).map { it.toDomain() }

  override suspend fun getByFixtureIdAndBatterId(fixtureId: Int, batterId: Int): List<Stat> =
    dao.getByFixtureIdAndBatterId(fixtureId, batterId).map { it.toDomain() }

  override suspend fun getByFixtureIdAndPitcherId(fixtureId: Int, pitcherId: Int): List<Stat> =
    dao.getByFixtureIdAndPitcherId(fixtureId, pitcherId).map { it.toDomain() }

  override suspend fun insertAll(items: List<Stat>) {
    dao.insertAll(items.map { it.toEntity() })
  }

  override suspend fun deleteByFixtureId(fixtureId: Int) {
    dao.deleteByFixtureId(fixtureId)
  }
}
