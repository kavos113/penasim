package com.example.penasim.domain

data class FielderAppointment(
    val teamId: Int,
    val playerId: Int,
    val position: Position,
    val isMain: Boolean,
    val number: Int, // 打順
)
