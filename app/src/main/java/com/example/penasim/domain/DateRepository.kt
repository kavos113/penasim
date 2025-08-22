package com.example.penasim.domain

interface DateRepository {
    suspend fun getDate(id: Int): Date?
    suspend fun getDateByMonthAndDay(month: Int, day: Int): Date?
}