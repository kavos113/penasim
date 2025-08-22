package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.GameMasterEntity

@Dao
interface GameMasterDao {
    @Query("SELECT * FROM game_masters WHERE id = :id")
    suspend fun getById(id: Int): GameMasterEntity?

    @Query("SELECT * FROM game_masters WHERE dateId = :dateId")
    suspend fun getByDateId(dateId: Int): List<GameMasterEntity>

    @Query("SELECT * FROM game_masters WHERE homeTeamId = :teamId OR awayTeamId = :teamId")
    suspend fun getByTeamId(teamId: Int): List<GameMasterEntity>

    @Query("SELECT * FROM game_masters")
    suspend fun getAll(): List<GameMasterEntity>
}