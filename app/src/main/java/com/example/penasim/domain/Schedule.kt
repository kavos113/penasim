package com.example.penasim.domain

// not include year, time
data class Schedule(
    val id: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int, // 0: Sunday, 1: Monday, ..., 6: Saturday
    val games: List<Game>
)
