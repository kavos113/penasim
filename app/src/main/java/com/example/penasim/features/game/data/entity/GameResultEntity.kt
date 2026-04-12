package com.example.penasim.features.game.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.penasim.features.schedule.data.entity.GameFixtureEntity

@Entity(
  tableName = "games",
  foreignKeys = [
    ForeignKey(
      entity = GameFixtureEntity::class,
      parentColumns = ["id"],
      childColumns = ["gameFixtureId"],
      onDelete = ForeignKey.CASCADE
    )
  ]
)
data class GameResultEntity(
  @PrimaryKey val gameFixtureId: Int,
  val homeScore: Int,
  val awayScore: Int,
)
