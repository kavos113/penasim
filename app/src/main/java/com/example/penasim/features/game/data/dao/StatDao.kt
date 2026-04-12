package com.example.penasim.features.game.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.penasim.data.entity.StatEntity

@Dao
interface StatDao {
  @Query("SELECT * FROM stats WHERE gameFixtureId = :fixtureId")
  suspend fun getByFixtureId(fixtureId: Int): List<StatEntity>

  @Query("SELECT * FROM stats WHERE batterId = :batterId")
  suspend fun getByBatterId(batterId: Int): List<StatEntity>

  @Query("SELECT * FROM stats WHERE pitcherId = :pitcherId")
  suspend fun getByPitcherId(pitcherId: Int): List<StatEntity>

  @Query("SELECT * FROM stats WHERE gameFixtureId = :fixtureId AND batterId = :batterId")
  suspend fun getByFixtureIdAndBatterId(fixtureId: Int, batterId: Int): List<StatEntity>

  @Query("SELECT * FROM stats WHERE gameFixtureId = :fixtureId AND pitcherId = :pitcherId")
  suspend fun getByFixtureIdAndPitcherId(fixtureId: Int, pitcherId: Int): List<StatEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<StatEntity>)

  @Query("DELETE FROM stats WHERE gameFixtureId = :fixtureId")
  suspend fun deleteByFixtureId(fixtureId: Int)
}
