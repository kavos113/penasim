package com.example.penasim.features.game.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "stats",
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
      childColumns = ["batterId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = PlayerEntity::class,
      parentColumns = ["id"],
      childColumns = ["pitcherId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["gameFixtureId"]),
    Index(value = ["batterId"]),
    Index(value = ["pitcherId"])
  ]
)
data class StatEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val gameFixtureId: Int,
  val batterId: Int,
  val pitcherId: Int,
  val inning: Int,
  val outCount: Int,
  val hitCount: Int,
  val earnedRun: Int,
  val result: String,
)
