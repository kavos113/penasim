package com.example.penasim.data.mapper

import com.example.penasim.data.entity.MainMemberEntity
import com.example.penasim.domain.MainMember

fun MainMemberEntity.toDomain(): MainMember = MainMember(
    teamId = teamId,
    playerId = playerId,
    memberType = memberType,
    isFielder = isFielder,
)

fun MainMember.toEntity(): MainMemberEntity = MainMemberEntity(
    teamId = teamId,
    playerId = playerId,
    memberType = memberType,
    isFielder = isFielder,
)
