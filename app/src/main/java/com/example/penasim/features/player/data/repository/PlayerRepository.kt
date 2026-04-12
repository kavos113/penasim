package com.example.penasim.data.repository

import com.example.penasim.data.dao.PlayerDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.domain.Player
import com.example.penasim.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayerRepository @Inject constructor(
  private val playerDao: PlayerDao
) : PlayerRepository {
  override suspend fun getPlayerCount(teamId: Int): Int = playerDao.getCountByTeamId(teamId)

  override suspend fun getPlayers(teamId: Int): List<Player> =
    playerDao.getByTeamId(teamId).map { it.toDomain() }

  override suspend fun getPlayer(id: Int): Player? =
    playerDao.getById(id)?.toDomain()

  override suspend fun getAllPlayers(): List<Player> =
    playerDao.getAll().map { it.toDomain() }
}
