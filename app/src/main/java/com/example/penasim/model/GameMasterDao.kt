package com.example.penasim.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameMasterDao {
    @Query("SELECT * FROM game_master")
    fun getAll(): List<GameMaster>

    @Query("SELECT * FROM game_master WHERE id = :id")
    fun getById(id: Int): GameMaster?

    @Query("SELECT * FROM game_master WHERE totalDay = :totalDay")
    fun getByTotalDay(totalDay: Int): List<GameMaster>

    @Query("SELECT * FROM game_master WHERE month = :month AND day = :day")
    fun getByDate(month: Int, day: Int): List<GameMaster>

    @Insert
    fun insert(gameMaster: GameMaster)

    @Delete
    fun delete(gameMaster: GameMaster)
}