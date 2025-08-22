package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = GameMasterEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameMasterId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class GameEntity(
    @PrimaryKey val id: Int,
    val gameMasterId: Int,
    val homeScore: Int,
    val awayScore: Int,
)
