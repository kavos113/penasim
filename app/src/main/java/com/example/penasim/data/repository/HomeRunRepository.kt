package com.example.penasim.data.repository

import com.example.penasim.data.dao.HomeRunDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.data.mapper.toEntity
import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.repository.HomeRunRepository
import javax.inject.Inject

class HomeRunRepository @Inject constructor(
  private val dao: HomeRunDao
) : HomeRunRepository {
  override suspend fun getHomeRunsByFixtureId(fixtureId: Int): List<HomeRun> =
    dao.getByFixtureId(fixtureId).map { it.toDomain() }

  override suspend fun getHomeRunsByPlayerId(playerId: Int): List<HomeRun> =
    dao.getByPlayerId(playerId).map { it.toDomain() }

  override suspend fun insertHomeRuns(homeRuns: List<HomeRun>) {
    dao.insertAll(homeRuns.map { it.toEntity() })
  }
}
