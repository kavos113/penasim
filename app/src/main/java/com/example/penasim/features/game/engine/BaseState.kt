package com.example.penasim.features.game.engine

data class BaseState(
  var firstBaseId: Int? = null,
  var secondBaseId: Int? = null,
  var thirdBaseId: Int? = null
) {
  fun reset() {
    firstBaseId = null
    secondBaseId = null
    thirdBaseId = null
  }

  fun single(batter: Int): Int {
    var scoreCount = 0
    if (thirdBaseId != null) {
      scoreCount++
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      thirdBaseId = secondBaseId
      secondBaseId = null
    }
    if (firstBaseId != null) {
      secondBaseId = firstBaseId
    }
    firstBaseId = batter
    return scoreCount
  }

  fun double(batter: Int): Int {
    var scoreCount = 0
    if (thirdBaseId != null) {
      scoreCount++
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      scoreCount++
      secondBaseId = null
    }
    if (firstBaseId != null) {
      thirdBaseId = firstBaseId
      firstBaseId = null
    }
    secondBaseId = batter
    return scoreCount
  }

  fun triple(batter: Int): Int {
    var scoreCount = 0
    if (thirdBaseId != null) {
      scoreCount++
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      scoreCount++
      secondBaseId = null
    }
    if (firstBaseId != null) {
      scoreCount++
      firstBaseId = null
    }
    thirdBaseId = batter
    return scoreCount
  }

  fun homeRun(): Int {
    var scoreCount = 1
    if (thirdBaseId != null) {
      scoreCount++
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      scoreCount++
      secondBaseId = null
    }
    if (firstBaseId != null) {
      scoreCount++
      firstBaseId = null
    }
    return scoreCount
  }
}
