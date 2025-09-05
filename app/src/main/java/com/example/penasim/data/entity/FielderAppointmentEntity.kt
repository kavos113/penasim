package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position

@Entity(
    tableName = "fielder_appointments",
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
data class FielderAppointmentEntity(
    val teamId: Int,
    @PrimaryKey val playerId: Int,
    val position: Position,
    val isMain: Boolean,
    val number: Int,
    val orderType: OrderType
)
