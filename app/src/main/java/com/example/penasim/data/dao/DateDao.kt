package com.example.penasim.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.penasim.data.entity.DateEntity

@Dao
interface DateDao {
    @Query("SELECT * FROM dates WHERE id = :id")
    suspend fun getById(id: Int): DateEntity?

    @Query("SELECT * FROM dates WHERE month = :month AND day = :day")
    suspend fun getByMonthAndDay(month: Int, day: Int): DateEntity?
}