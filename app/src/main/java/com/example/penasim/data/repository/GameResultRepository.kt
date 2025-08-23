package com.example.penasim.data.repository

import com.example.penasim.data.dao.GameResultDao
import com.example.penasim.data.entity.GameResultEntity
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.repository.GameResultRepository

class GameResultRepository(
    private val gameResultDao: GameResultDao
): GameResultRepository {
    override suspend fun getGameByFixtureId(fixtureId: Int): GameResult?
        = gameResultDao.getByGameFixtureId(fixtureId)?.toDomain()

    override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult>
        = gameResultDao.getByGameFixtureIds(fixtureIds).map { it.toDomain() }

    override suspend fun getAllGames(): List<GameResult>
        = gameResultDao.getAll().map { it.toDomain() }

    override suspend fun createGame(
        fixtureId: Int,
        homeScore: Int,
        awayScore: Int
    ): GameResult? {
        if (getGameByFixtureId(fixtureId) != null) {
            // Game already exists for this fixture
            return null
        }

        gameResultDao.insert(GameResultEntity(
            gameFixtureId = fixtureId,
            homeScore = homeScore,
            awayScore = awayScore
        ))

        return getGameByFixtureId(fixtureId)
    }
}