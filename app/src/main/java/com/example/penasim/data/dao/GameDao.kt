package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.penasim.data.entity.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: Int): GameEntity?

    @Query("SELECT * FROM games")
    suspend fun getAll(): List<GameEntity>

    @Query("SELECT * FROM games WHERE gameMasterId = :gameMasterId")
    suspend fun getByGameMasterId(gameMasterId: Int): List<GameEntity>

    @Query("SELECT * FROM games WHERE gameMasterId IN (:gameMasterIds)")
    suspend fun getByGameMasterIds(gameMasterIds: List<Int>): List<GameEntity>

    @Insert
    suspend fun insert(game: GameEntity): Long
}