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

    @Query("SELECT * FROM games WHERE gameFixtureId = :gameFixtureId")
    suspend fun getByGameFixtureId(gameFixtureId: Int): List<GameEntity>

    @Query("SELECT * FROM games WHERE gameFixtureId IN (:gameFixtureIds)")
    suspend fun getByGameFixtureIds(gameFixtureIds: List<Int>): List<GameEntity>

    @Insert
    suspend fun insert(game: GameEntity): Long
}