package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "pitching_stats",
    primaryKeys = ["gameFixtureId", "playerId"],
    foreignKeys = [
        ForeignKey(
            entity = GameFixtureEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameFixtureId"],
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
data class PitchingStatEntity(
    val gameFixtureId: Int,
    val playerId: Int,
    val inningPitched: Int,
    val hit: Int,
    val run: Int,
    val earnedRun: Int,
    val walk: Int,
    val strikeOut: Int,
    val homeRun: Int,
    val win: Boolean,
    val loss: Boolean,
    val hold: Boolean,
    val save: Boolean,
)
