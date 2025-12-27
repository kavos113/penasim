package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.TeamEntity

@Dao
interface TeamDao {
  @Query("SELECT * FROM teams WHERE id = :id")
  suspend fun getById(id: Int): TeamEntity?

  @Query("SELECT * FROM teams WHERE leagueId = :leagueId")
  suspend fun getByLeagueId(leagueId: Int): List<TeamEntity>

  @Query("SELECT * FROM teams")
  suspend fun getAll(): List<TeamEntity>
}