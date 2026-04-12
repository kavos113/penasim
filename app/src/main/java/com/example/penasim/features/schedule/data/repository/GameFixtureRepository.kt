package com.example.penasim.features.schedule.data.repository

import com.example.penasim.features.schedule.data.dao.GameFixtureDao
import com.example.penasim.features.schedule.data.mapper.toDomain
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.schedule.domain.repository.GameFixtureRepository
import java.time.LocalDate
import javax.inject.Inject

class GameFixtureRepository @Inject constructor(
  private val gameFixtureDao: GameFixtureDao,
) : GameFixtureRepository {
  override suspend fun getGameFixture(id: Int): GameFixture? =
    gameFixtureDao.getById(id)?.toDomain()

  override suspend fun getGameFixturesByDate(date: LocalDate): List<GameFixture> =
    gameFixtureDao.getByDate(date).map { it.toDomain() }

  override suspend fun getGameFixturesByTeam(team: Team): List<GameFixture> =
    gameFixtureDao.getByTeamId(team.id).map { it.toDomain() }

  override suspend fun getAllGameFixtures(): List<GameFixture> =
    gameFixtureDao.getAll().map { it.toDomain() }
}
