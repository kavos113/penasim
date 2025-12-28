package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position

@Entity(
  tableName = "fielder_appointments",
  primaryKeys = ["playerId", "orderType"],
  foreignKeys = [
    ForeignKey(
      entity = TeamEntity::class,
      parentColumns = ["id"],
      childColumns = ["teamId"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PlayerEntity::class,
      parentColumns = ["id"],
      childColumns = ["playerId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index(value = ["teamId"]),
    Index(value = ["playerId"])
  ]
)
data class FielderAppointmentEntity(
  val teamId: Int,
  val playerId: Int,
  val position: Position,
  val number: Int,
  val orderType: OrderType
)
