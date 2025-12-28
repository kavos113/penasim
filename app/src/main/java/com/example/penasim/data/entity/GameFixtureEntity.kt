package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.penasim.data.repository.Converters
import java.time.LocalDate

@Entity(
  tableName = "game_fixtures",
  foreignKeys = [
    ForeignKey(
      entity = TeamEntity::class,
      parentColumns = ["id"],
      childColumns = ["homeTeamId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = TeamEntity::class,
      parentColumns = ["id"],
      childColumns = ["awayTeamId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["homeTeamId"]),
    Index(value = ["awayTeamId"])
  ]
)
@TypeConverters(Converters::class)
data class GameFixtureEntity(
  @PrimaryKey val id: Int,
  val date: LocalDate,
  val numberOfGames: Int,
  val homeTeamId: Int,
  val awayTeamId: Int,
)
