package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
