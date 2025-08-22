package com.example.penasim.data.mapper

import com.example.penasim.data.entity.DateEntity
import com.example.penasim.domain.Date

fun DateEntity.toDomain(): Date = Date(
    id = id,
    month = month,
    day = day,
    dayOfWeek = dayOfWeek
)

fun Date.toEntity(): DateEntity = DateEntity(
    id = id,
    month = month,
    day = day,
    dayOfWeek = dayOfWeek
)