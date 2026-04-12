package com.example.penasim.features.command.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.penasim.features.command.domain.OrderType
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.player.data.entity.PlayerEntity
import com.example.penasim.features.team.data.entity.TeamEntity

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
