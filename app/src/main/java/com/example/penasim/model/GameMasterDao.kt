package com.example.penasim.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameMasterDao {
    @Query("SELECT * FROM GameMaster")
    fun getAll(): List<GameMaster>

    @Query("SELECT * FROM GameMaster WHERE totalDay = :totalDay")
    fun getByTotalDay(totalDay: Int): GameMaster?

    @Query("SELECT * FROM GameMaster WHERE month = :month AND day = :day")
    fun getByDate(month: Int, day: Int): List<GameMaster>

    @Insert
    fun insert(gameMaster: GameMaster)

    @Delete
    fun delete(gameMaster: GameMaster)
}