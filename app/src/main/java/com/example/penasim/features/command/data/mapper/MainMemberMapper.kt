package com.example.penasim.features.command.data.mapper

import com.example.penasim.features.command.data.entity.MainMemberEntity
import com.example.penasim.features.command.domain.MainMember

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
