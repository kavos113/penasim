package com.example.penasim.features.game.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.penasim.features.schedule.data.entity.GameFixtureEntity
import com.example.penasim.features.team.data.entity.TeamEntity

@Entity(
  tableName = "inning_scores",
  primaryKeys = ["gameFixtureId", "teamId", "inning"],
  foreignKeys = [
    ForeignKey(
      entity = GameFixtureEntity::class,
      parentColumns = ["id"],
      childColumns = ["gameFixtureId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = TeamEntity::class,
      parentColumns = ["id"],
      childColumns = ["teamId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["gameFixtureId"]),
    Index(value = ["teamId"])
  ]
)
data class InningScoreEntity(
  val gameFixtureId: Int,
  val teamId: Int,
  val inning: Int,
  val score: Int,
)
