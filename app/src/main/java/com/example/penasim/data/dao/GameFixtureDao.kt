package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.GameFixtureEntity
import java.time.LocalDate

@Dao
interface GameFixtureDao {
    @Query("SELECT * FROM game_masters WHERE id = :id")
    suspend fun getById(id: Int): GameFixtureEntity?

    @Query("SELECT * FROM game_masters WHERE date = :date")
    suspend fun getByDate(date: LocalDate): List<GameFixtureEntity>

    @Query("SELECT * FROM game_masters WHERE homeTeamId = :teamId OR awayTeamId = :teamId")
    suspend fun getByTeamId(teamId: Int): List<GameFixtureEntity>

    @Query("SELECT * FROM game_masters")
    suspend fun getAll(): List<GameFixtureEntity>
}