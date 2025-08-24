package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class PlayerEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val teamId: Int,

    val meet: Int,
    val power: Int,
    val speed: Int,
    val throwing: Int,
    val defense: Int,
    val catching: Int,

    val ballSpeed: Int,
    val control: Int,
    val stamina: Int,
)
