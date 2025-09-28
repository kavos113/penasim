package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.penasim.data.entity.PitchingStatEntity

@Dao
interface PitchingStatDao {
    @Query("SELECT * FROM pitching_stats WHERE gameFixtureId = :fixtureId")
    suspend fun getByFixtureId(fixtureId: Int): List<PitchingStatEntity>

    @Query("SELECT * FROM pitching_stats WHERE gameFixtureId IN (:fixtureIds)")
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStatEntity>

    @Query("SELECT * FROM pitching_stats WHERE playerId = :playerId")
    suspend fun getByPlayerId(playerId: Int): List<PitchingStatEntity>

    @Query("SELECT * FROM pitching_stats WHERE playerId IN (:playerIds)")
    suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PitchingStatEntity>)

    @Query("DELETE FROM pitching_stats WHERE gameFixtureId = :fixtureId")
    suspend fun deleteByFixtureId(fixtureId: Int)
}
