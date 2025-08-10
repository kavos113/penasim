package com.example.penasim.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameMasterDao {
    @Query("SELECT * FROM game_master")
    suspend fun getAll(): List<GameMaster>

    @Query("SELECT * FROM game_master WHERE id = :id")
    suspend fun getById(id: Int): GameMaster?

    @Query("SELECT * FROM game_master WHERE totalDay = :totalDay")
    suspend fun getByTotalDay(totalDay: Int): List<GameMaster>

    @Query("SELECT * FROM game_master WHERE month = :month AND day = :day")
    suspend fun getByDate(month: Int, day: Int): List<GameMaster>

    @Insert
    suspend fun insert(gameMaster: GameMaster)

    @Delete
    suspend fun delete(gameMaster: GameMaster)
}