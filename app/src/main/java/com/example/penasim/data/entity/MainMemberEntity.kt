package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.penasim.domain.MemberType

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
  ]
)
data class MainMemberEntity(
  val teamId: Int,
  @PrimaryKey val playerId: Int,
  val memberType: MemberType,
  val isFielder: Boolean,
)
