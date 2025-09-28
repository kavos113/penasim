package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.penasim.domain.PitcherType

@Entity(
    tableName = "pitcher_appointments",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class PitcherAppointmentEntity(
    val teamId: Int,
    @PrimaryKey val playerId: Int,
    val type: PitcherType,
    val number: Int,
)
