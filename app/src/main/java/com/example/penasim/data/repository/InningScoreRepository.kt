package com.example.penasim.data.repository

import com.example.penasim.data.dao.InningScoreDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.data.mapper.toEntity
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.repository.InningScoreRepository
import javax.inject.Inject

class InningScoreRepository @Inject constructor(
  private val dao: InningScoreDao
) : InningScoreRepository {
  override suspend fun getByFixtureId(fixtureId: Int): List<InningScore> =
    dao.getByFixtureId(fixtureId).map { it.toDomain() }

  override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<InningScore> =
    dao.getByFixtureIds(fixtureIds).map { it.toDomain() }

  override suspend fun getByTeamId(teamId: Int): List<InningScore> =
    dao.getByTeamId(teamId).map { it.toDomain() }

  override suspend fun getByTeamIds(teamIds: List<Int>): List<InningScore> =
    dao.getByTeamIds(teamIds).map { it.toDomain() }

  override suspend fun insertAll(items: List<InningScore>) {
    dao.insertAll(items.map { it.toEntity() })
  }

  override suspend fun deleteByFixtureId(fixtureId: Int) {
    dao.deleteByFixtureId(fixtureId)
  }
}
