package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

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
    ]
)
data class InningScoreEntity(
    val gameFixtureId: Int,
    val teamId: Int,
    val inning: Int,
    val score: Int,
)
