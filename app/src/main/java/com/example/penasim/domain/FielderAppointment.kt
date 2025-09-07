package com.example.penasim.domain

data class FielderAppointment(
    val teamId: Int,
    val playerId: Int,
    val position: Position,
    val number: Int,
    val orderType: OrderType,
)

enum class OrderType {
    NORMAL,
    LEFT,
    DH,
    LEFT_DH,
}

fun FielderAppointment.isStarting(): Boolean = position != Position.BENCH && position != Position.SUBSTITUTE