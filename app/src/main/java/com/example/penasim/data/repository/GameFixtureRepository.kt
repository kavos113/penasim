package com.example.penasim.data.repository

import com.example.penasim.data.dao.GameFixtureDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import java.time.LocalDate

class GameFixtureRepository(
    private val gameFixtureDao: GameFixtureDao,
): GameFixtureRepository {
    override suspend fun getGameFixture(id: Int): GameFixture? = gameFixtureDao.getById(id)?.toDomain()

    override suspend fun getGameFixturesByDate(date: LocalDate): List<GameFixture>
        = gameFixtureDao.getByDate(date).map { it.toDomain() }

    override suspend fun getGameFixturesByTeam(team: Team): List<GameFixture>
        = gameFixtureDao.getByTeamId(team.id).map { it.toDomain() }

    override suspend fun getAllGameFixtures(): List<GameFixture>
        = gameFixtureDao.getAll().map { it.toDomain() }
}