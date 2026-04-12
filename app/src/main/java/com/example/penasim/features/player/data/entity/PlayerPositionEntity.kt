package com.example.penasim.features.player.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.penasim.features.player.domain.Position

@Entity(
  tableName = "player_positions",
  primaryKeys = ["playerId", "position"],
  foreignKeys = [
    ForeignKey(
      entity = PlayerEntity::class,
      parentColumns = ["id"],
      childColumns = ["playerId"],
      onDelete = ForeignKey.CASCADE,
    )
  ]
)
data class PlayerPositionEntity(
  val playerId: Int,
  val position: Position,
  val defense: Int,
)
