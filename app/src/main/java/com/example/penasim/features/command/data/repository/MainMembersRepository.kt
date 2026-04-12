package com.example.penasim.data.repository

import com.example.penasim.data.dao.MainMemberDao
import com.example.penasim.data.mapper.toDomain
import com.example.penasim.data.mapper.toEntity
import com.example.penasim.domain.MainMember
import com.example.penasim.domain.repository.MainMembersRepository
import javax.inject.Inject

class MainMembersRepository @Inject constructor(
  private val mainMemberDao: MainMemberDao
) : MainMembersRepository {
  override suspend fun getMainMembersByTeamId(teamId: Int): List<MainMember> =
    mainMemberDao.getByTeamId(teamId).map { it.toDomain() }

  override suspend fun getMainMemberByPlayerId(playerId: Int): MainMember? =
    mainMemberDao.getByPlayerId(playerId)?.toDomain()

  override suspend fun insertMainMember(mainMember: MainMember) =
    mainMemberDao.insert(mainMember.toEntity())

  override suspend fun insertMainMembers(mainMembers: List<MainMember>) =
    mainMemberDao.insertAll(mainMembers.map { it.toEntity() })

  override suspend fun deleteMainMember(mainMember: MainMember) =
    mainMemberDao.delete(mainMember.toEntity())

  override suspend fun deleteMainMembers(mainMembers: List<MainMember>) =
    mainMemberDao.deleteAll(mainMembers.map { it.toEntity() })

  override suspend fun updateMainMember(mainMember: MainMember) =
    mainMemberDao.update(mainMember.toEntity())

  override suspend fun updateMainMembers(mainMembers: List<MainMember>) =
    mainMemberDao.updateAll(mainMembers.map { it.toEntity() })
}
