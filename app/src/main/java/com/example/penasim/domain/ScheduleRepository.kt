package com.example.penasim.domain

interface ScheduleRepository {
    fun getSchedule(id: Int): Schedule?
    fun getScheduleByDate(month: Int, day: Int): Schedule?
}