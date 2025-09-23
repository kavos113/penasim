package com.example.penasim.domain

data class MainMember(
    val teamId: Int,
    val playerId: Int,
    val memberType: MemberType,
    val isFielder: Boolean,
)

enum class MemberType {
    MAIN,
    SUB,
}
