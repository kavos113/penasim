package com.example.penasim.data.repository

import androidx.room.TypeConverter
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Position
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromPosition(position: Position?): String? = position?.name

    @TypeConverter
    fun toPosition(name: String?): Position? = name?.let { Position.valueOf(it) }

    @TypeConverter
    fun fromPitcherType(type: PitcherType?): String? = type?.name

    @TypeConverter
    fun toPitcherType(name: String?): PitcherType? = name?.let { PitcherType.valueOf(it) }
}