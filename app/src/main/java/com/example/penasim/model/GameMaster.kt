package com.example.penasim.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_master")
data class GameMaster(
    @PrimaryKey val id: Int,
    val totalDay: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int, // 0: monday, 1: tuesday, ..., 6: sunday
    val numberOfGames: Int, // 節内で何番目か
    val homeTeamId: Int,
    val awayTeamId: Int,
)
