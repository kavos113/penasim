package com.example.penasim.data.repository

import com.example.penasim.data.dao.DateDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.domain.Date
import com.example.penasim.domain.repository.DateRepository

class DateRepository(
    private val dateDao: DateDao
): DateRepository {
    override suspend fun getDate(id: Int): Date? = dateDao.getById(id)?.toDomain()

    override suspend fun getDateByMonthAndDay(
        month: Int,
        day: Int
    ): Date? = dateDao.getByMonthAndDay(month, day)?.toDomain()
}