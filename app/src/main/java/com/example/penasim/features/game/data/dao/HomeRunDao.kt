package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.penasim.data.entity.HomeRunEntity

@Dao
interface HomeRunDao {
  @Query("SELECT * FROM home_runs WHERE fixtureId = :fixtureId")
  suspend fun getByFixtureId(fixtureId: Int): List<HomeRunEntity>

  @Query("SELECT * FROM home_runs WHERE playerId = :playerId")
  suspend fun getByPlayerId(playerId: Int): List<HomeRunEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<HomeRunEntity>)

  @Query("DELETE FROM home_runs WHERE fixtureId = :fixtureId")
  suspend fun deleteByFixtureId(fixtureId: Int)
}
