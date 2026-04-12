package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.penasim.data.entity.FielderAppointmentEntity

@Dao
interface FielderAppointmentDao {
  @Query("SELECT * FROM fielder_appointments WHERE teamId = :teamId")
  suspend fun getByTeamId(teamId: Int): List<FielderAppointmentEntity>

  @Query("SELECT * FROM fielder_appointments WHERE playerId = :playerId")
  suspend fun getByPlayerId(playerId: Int): FielderAppointmentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entity: FielderAppointmentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(entities: List<FielderAppointmentEntity>)

  @Update
  suspend fun update(entity: FielderAppointmentEntity)

  @Update
  suspend fun updateAll(entities: List<FielderAppointmentEntity>)

  @Delete
  suspend fun delete(entity: FielderAppointmentEntity)

  @Delete
  suspend fun deleteAll(entities: List<FielderAppointmentEntity>)
}
