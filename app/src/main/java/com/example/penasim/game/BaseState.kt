package com.example.penasim.game

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

  // return score count
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

  // return score count
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

  // return score count
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

  // return score count
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