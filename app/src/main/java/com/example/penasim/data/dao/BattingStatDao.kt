package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.penasim.data.entity.BattingStatEntity

@Dao
interface BattingStatDao {
    @Query("SELECT * FROM batting_stats WHERE gameFixtureId = :fixtureId")
    suspend fun getByFixtureId(fixtureId: Int): List<BattingStatEntity>

    @Query("SELECT * FROM batting_stats WHERE gameFixtureId IN (:fixtureIds)")
    suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BattingStatEntity>)

    @Query("DELETE FROM batting_stats WHERE gameFixtureId = :fixtureId")
    suspend fun deleteByFixtureId(fixtureId: Int)
}
