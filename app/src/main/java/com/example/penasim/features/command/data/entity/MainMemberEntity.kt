package com.example.penasim.features.command.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.penasim.features.command.domain.MemberType
import com.example.penasim.features.player.data.entity.PlayerEntity
import com.example.penasim.features.team.data.entity.TeamEntity

@Entity(
  tableName = "main_members",
  foreignKeys = [
    ForeignKey(
      entity = TeamEntity::class,
      parentColumns = ["id"],
      childColumns = ["teamId"],
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
    Index(value = ["teamId"]),
    Index(value = ["playerId"])
  ]
)
data class MainMemberEntity(
  val teamId: Int,
  @PrimaryKey val playerId: Int,
  val memberType: MemberType,
  val isFielder: Boolean,
)
