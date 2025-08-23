package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = GameFixtureEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameFixtureId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class GameEntity(
    @PrimaryKey val id: Int,
    val gameFixtureId: Int,
    val homeScore: Int,
    val awayScore: Int,
)
