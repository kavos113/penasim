package com.example.penasim.core.session

import kotlinx.coroutines.flow.StateFlow

interface SelectedTeamStore {
  val teamId: StateFlow<Int>

  fun currentTeamId(): Int

  fun setTeamId(teamId: Int)
}
