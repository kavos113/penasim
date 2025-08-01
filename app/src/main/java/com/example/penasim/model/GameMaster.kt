package com.example.penasim.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameMaster(
    @PrimaryKey val totalDay: Int,
    val month: Int,
    val day: Int,
    val numberOfGames: Int, // 節内で何番目か
    val homeTeamId: Int,
    val awayTeamId: Int,
)
