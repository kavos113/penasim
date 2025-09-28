package com.example.penasim.data.dao

import androidx.room.*
import com.example.penasim.data.entity.MainMemberEntity

@Dao
interface MainMemberDao {
    @Query("SELECT * FROM main_members WHERE teamId = :teamId")
    suspend fun getByTeamId(teamId: Int): List<MainMemberEntity>

    @Query("SELECT * FROM main_members WHERE playerId = :playerId")
    suspend fun getByPlayerId(playerId: Int): MainMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MainMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MainMemberEntity>)

    @Update
    suspend fun update(entity: MainMemberEntity)

    @Update
    suspend fun updateAll(entities: List<MainMemberEntity>)

    @Delete
    suspend fun delete(entity: MainMemberEntity)

    @Delete
    suspend fun deleteAll(entities: List<MainMemberEntity>)
}
