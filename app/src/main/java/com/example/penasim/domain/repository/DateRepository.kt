package com.example.penasim.domain.repository

import com.example.penasim.domain.Date

interface DateRepository {
    suspend fun getDate(id: Int): Date?
    suspend fun getDateByMonthAndDay(month: Int, day: Int): Date?
}