package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "home_runs",
    primaryKeys = ["fixtureId", "playerId", "inning", "count"],
    foreignKeys = [
        ForeignKey(
            entity = GameFixtureEntity::class,
            parentColumns = ["id"],
            childColumns = ["fixtureId"],
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
data class HomeRunEntity(
    val fixtureId: Int,
    val playerId: Int,
    val inning: Int,
    val count: Int,
)
