package com.example.penasim.features.player.domain.repository

import com.example.penasim.features.player.domain.PlayerPosition
import com.example.penasim.features.player.domain.Position

interface PlayerPositionRepository {
  suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition>
  suspend fun getAllPlayerPositions(): List<PlayerPosition>
  suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition>
}