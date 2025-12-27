package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.penasim.data.entity.PitcherAppointmentEntity

@Dao
interface PitcherAppointmentDao {
  @Query("SELECT * FROM pitcher_appointments WHERE teamId = :teamId")
  suspend fun getByTeamId(teamId: Int): List<PitcherAppointmentEntity>

  @Query("SELECT * FROM pitcher_appointments WHERE playerId = :playerId")
  suspend fun getByPlayerId(playerId: Int): PitcherAppointmentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entity: PitcherAppointmentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(entities: List<PitcherAppointmentEntity>)

  @Update
  suspend fun update(entity: PitcherAppointmentEntity)

  @Update
  suspend fun updateAll(entities: List<PitcherAppointmentEntity>)

  @Delete
  suspend fun delete(entity: PitcherAppointmentEntity)

  @Delete
  suspend fun deleteAll(entities: List<PitcherAppointmentEntity>)
}
