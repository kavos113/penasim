package com.example.penasim.data.repository

import com.example.penasim.data.dao.PlayerPositionDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position
import com.example.penasim.domain.repository.PlayerPositionRepository
import javax.inject.Inject

class PlayerPositionRepository @Inject constructor(
    private val playerPositionDao: PlayerPositionDao
) : PlayerPositionRepository {
    override suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition> =
        playerPositionDao.getByPlayerId(playerId).map { it.toDomain() }

    override suspend fun getAllPlayerPositions(): List<PlayerPosition> =
        playerPositionDao.getAll().map { it.toDomain() }

    override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> =
        playerPositionDao.getAllByPosition(position).map { it.toDomain() }
}
