package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.PlayerEntity

@Dao
interface PlayerDao {
  @Query("SELECT COUNT(*) FROM players WHERE teamId = :teamId")
  suspend fun getCountByTeamId(teamId: Int): Int

  @Query("SELECT * FROM players WHERE teamId = :teamId")
  suspend fun getByTeamId(teamId: Int): List<PlayerEntity>

  @Query("SELECT * FROM players WHERE id = :id")
  suspend fun getById(id: Int): PlayerEntity?

  @Query("SELECT * FROM players")
  suspend fun getAll(): List<PlayerEntity>
}
