package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.PlayerPositionEntity
import com.example.penasim.domain.Position

@Dao
interface PlayerPositionDao {
  @Query("SELECT * FROM player_positions WHERE playerId = :playerId")
  suspend fun getByPlayerId(playerId: Int): List<PlayerPositionEntity>

  @Query("SELECT * FROM player_positions")
  suspend fun getAll(): List<PlayerPositionEntity>

  @Query("SELECT * FROM player_positions WHERE position = :position")
  suspend fun getAllByPosition(position: Position): List<PlayerPositionEntity>
}
