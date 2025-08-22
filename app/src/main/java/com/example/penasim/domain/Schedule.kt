package com.example.penasim.domain

data class Schedule(
    val id: Int,
    val date: Date,
    val games: List<Game>,
)
