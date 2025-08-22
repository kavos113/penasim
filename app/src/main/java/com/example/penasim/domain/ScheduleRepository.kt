package com.example.penasim.domain

interface ScheduleRepository {
    suspend fun getSchedule(id: Int): Schedule?
    suspend fun getScheduleByDate(month: Int, day: Int): Schedule?
}