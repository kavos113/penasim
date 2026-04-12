package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
  @PrimaryKey val id: Int,
  val name: String,
  val leagueId: Int
)
