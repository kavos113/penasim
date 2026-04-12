package com.example.penasim.domain.repository

import com.example.penasim.domain.MainMember

interface MainMembersRepository {
  suspend fun getMainMembersByTeamId(teamId: Int): List<MainMember>
  suspend fun getMainMemberByPlayerId(playerId: Int): MainMember?

  suspend fun insertMainMember(mainMember: MainMember)
  suspend fun insertMainMembers(mainMembers: List<MainMember>)

  suspend fun deleteMainMember(mainMember: MainMember)
  suspend fun deleteMainMembers(mainMembers: List<MainMember>)

  suspend fun updateMainMember(mainMember: MainMember)
  suspend fun updateMainMembers(mainMembers: List<MainMember>)
}