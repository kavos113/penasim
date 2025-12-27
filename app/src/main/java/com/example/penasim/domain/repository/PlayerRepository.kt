package com.example.penasim.domain.repository

import com.example.penasim.domain.Player

interface PlayerRepository {
  suspend fun getPlayerCount(teamId: Int): Int
  suspend fun getPlayers(teamId: Int): List<Player>
  suspend fun getPlayer(id: Int): Player?
  suspend fun getAllPlayers(): List<Player>
}