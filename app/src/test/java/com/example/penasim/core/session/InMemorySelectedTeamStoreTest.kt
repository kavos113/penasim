package com.example.penasim.core.session

import kotlin.test.Test
import kotlin.test.assertEquals

class InMemorySelectedTeamStoreTest {

  @Test
  fun currentTeamId_returnsDefaultValue_beforeUpdate() {
    val store = InMemorySelectedTeamStore()

    assertEquals(0, store.currentTeamId())
    assertEquals(0, store.teamId.value)
  }

  @Test
  fun setTeamId_updatesCurrentValueAndFlow() {
    val store = InMemorySelectedTeamStore()

    store.setTeamId(7)

    assertEquals(7, store.currentTeamId())
    assertEquals(7, store.teamId.value)
  }
}


