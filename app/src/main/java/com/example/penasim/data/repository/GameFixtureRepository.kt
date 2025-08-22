package com.example.penasim.data.repository

import com.example.penasim.data.dao.GameFixtureDao
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import java.time.LocalDate

class GameFixtureRepository(
    private val gameMasterDao: GameFixtureDao,
): GameFixtureRepository {
    override suspend fun getGameMaster(id: Int): GameFixture? {
        TODO("Not yet implemented")
    }

    override suspend fun getGameMastersByDate(date: LocalDate): List<GameFixture> {
        TODO("Not yet implemented")
    }

    override suspend fun getGameMastersByTeam(team: Team): List<GameFixture> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllGameMasters(): List<GameFixture> {
        TODO("Not yet implemented")
    }
}