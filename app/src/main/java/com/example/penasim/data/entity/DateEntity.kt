package com.example.penasim.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dates")
data class DateEntity(
    @PrimaryKey val id: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int // 0: Sunday, 1: Monday, ..., 6: Saturday
)