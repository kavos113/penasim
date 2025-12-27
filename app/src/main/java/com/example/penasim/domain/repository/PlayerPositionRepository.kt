package com.example.penasim.domain.repository

import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.Position

interface PlayerPositionRepository {
  suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition>
  suspend fun getAllPlayerPositions(): List<PlayerPosition>
  suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition>
}