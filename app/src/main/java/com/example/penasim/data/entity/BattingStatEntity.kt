package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

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
