package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_masters",
    foreignKeys = [
        ForeignKey(
            entity = DateEntity::class,
            parentColumns = ["id"],
            childColumns = ["dateId"],
            onDelete = ForeignKey.CASCADE
        ),
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
    ]
)
data class GameMasterEntity(
    @PrimaryKey val id: Int,
    val dateId: Int,
    val numberOfGames: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
)
