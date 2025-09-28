package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.penasim.data.entity.InningScoreEntity

@Dao
interface InningScoreDao {
    @Query("SELECT * FROM inning_scores WHERE gameFixtureId = :fixtureId")
    suspend fun getByFixtureId(fixtureId: Int): List<InningScoreEntity>

    @Query("SELECT * FROM inning_scores WHERE gameFixtureId IN (:fixtureIds)")
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<InningScoreEntity>

    @Query("SELECT * FROM inning_scores WHERE teamId = :teamId")
    suspend fun getByTeamId(teamId: Int): List<InningScoreEntity>

    @Query("SELECT * FROM inning_scores WHERE teamId IN (:teamIds)")
    suspend fun getByTeamIds(teamIds: List<Int>): List<InningScoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InningScoreEntity>)

    @Query("DELETE FROM inning_scores WHERE gameFixtureId = :fixtureId")
    suspend fun deleteByFixtureId(fixtureId: Int)
}
