package com.example.penasim.features.game.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.penasim.features.player.data.entity.PlayerEntity
import com.example.penasim.features.schedule.data.entity.GameFixtureEntity

@Entity(
  tableName = "batting_stats",
  primaryKeys = ["gameFixtureId", "playerId"],
  foreignKeys = [
    ForeignKey(
      entity = GameFixtureEntity::class,
      parentColumns = ["id"],
      childColumns = ["gameFixtureId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = PlayerEntity::class,
      parentColumns = ["id"],
      childColumns = ["playerId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["gameFixtureId"]),
    Index(value = ["playerId"])
  ]
)
data class BattingStatEntity(
  val gameFixtureId: Int,
  val playerId: Int,
  val atBat: Int,
  val hit: Int,
  val doubleHit: Int,
  val tripleHit: Int,
  val homeRun: Int,
  val walk: Int,
  val rbi: Int,
  val strikeOut: Int,
)
