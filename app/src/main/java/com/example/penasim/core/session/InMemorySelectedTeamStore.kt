package com.example.penasim.core.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySelectedTeamStore @Inject constructor() : SelectedTeamStore {
  private val _teamId = MutableStateFlow(DEFAULT_TEAM_ID)
  override val teamId: StateFlow<Int> = _teamId.asStateFlow()

  override fun currentTeamId(): Int = _teamId.value

  override fun setTeamId(teamId: Int) {
    _teamId.value = teamId
  }

  companion object {
    private const val DEFAULT_TEAM_ID = 0
  }
}
