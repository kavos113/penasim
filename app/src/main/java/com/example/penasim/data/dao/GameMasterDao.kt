package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.GameMasterEntity
import java.time.LocalDate

@Dao
interface GameMasterDao {
    @Query("SELECT * FROM game_masters WHERE id = :id")
    suspend fun getById(id: Int): GameMasterEntity?

    @Query("SELECT * FROM game_masters WHERE date = :date")
    suspend fun getByDate(date: LocalDate): List<GameMasterEntity>

    @Query("SELECT * FROM game_masters WHERE homeTeamId = :teamId OR awayTeamId = :teamId")
    suspend fun getByTeamId(teamId: Int): List<GameMasterEntity>

    @Query("SELECT * FROM game_masters")
    suspend fun getAll(): List<GameMasterEntity>
}