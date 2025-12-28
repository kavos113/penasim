package com.example.penasim.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

// 押している間に一定間隔で関数を実行する
@Composable
fun PressingButton(
  buttonText: String,
  intervalMs: Long,
  function: () -> Unit,
) {
  val interactionSource = remember {
    MutableInteractionSource()
  }
  val isPressed by interactionSource.collectIsPressedAsState()

  LaunchedEffect(isPressed) {
    if (isPressed) {
      while (true) {
        function()
        delay(intervalMs)
      }
    }
  }

  Button(
    onClick = {},
    interactionSource = interactionSource
  ) {
    Text(text = buttonText)
  }
}