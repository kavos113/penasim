package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.penasim.data.entity.GameResultEntity

@Dao
interface GameResultDao {
  @Query("SELECT * FROM games")
  suspend fun getAll(): List<GameResultEntity>

  @Query("SELECT * FROM games WHERE gameFixtureId = :gameFixtureId")
  suspend fun getByGameFixtureId(gameFixtureId: Int): GameResultEntity?

  @Query("SELECT * FROM games WHERE gameFixtureId IN (:gameFixtureIds)")
  suspend fun getByGameFixtureIds(gameFixtureIds: List<Int>): List<GameResultEntity>

  @Query("DELETE FROM games")
  suspend fun deleteAll()

  @Insert
  suspend fun insert(game: GameResultEntity): Long
}