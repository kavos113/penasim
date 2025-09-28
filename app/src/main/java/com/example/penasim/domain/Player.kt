package com.example.penasim.domain

data class Player(
    val id: Int,
    val firstName: String, // "Pitcher"は野手オーダー用
    val lastName: String,
    val teamId: Int,

    val meet: Int,
    val power: Int,
    val speed: Int,
    val throwing: Int,
    val defense: Int,
    val catching: Int,

    val ballSpeed: Int,
    val control: Int,
    val stamina: Int,

    val starter: Int,
    val reliever: Int,
)