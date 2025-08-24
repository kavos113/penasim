package com.example.penasim.ui.command

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.penasim.ui.theme.playerBorderColor


@Composable
internal fun OrderPlayerItem(
    player: DisplayFielder,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = playerBorderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.number.toString(),
                fontSize = 16.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(3f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = playerBorderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(color = player.color)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            SpacedText(
                text = player.displayName,
                fontSize = 16.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = playerBorderColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(color = player.color)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.position,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
internal fun SimplePlayerItem(
    displayName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(40.dp)
            .border(
                width = 1.dp,
                color = playerBorderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .background(color = color)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .fillMaxWidth()
            .then(modifier)
    ) {
        SpacedText(
            text = displayName,
            fontSize = 16.sp,
        )
    }
}

@Composable
internal fun Status(
    value: Int,
    alphabet: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = alphabet,
                fontSize = 24.sp,
                color = color
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = value.toString(),
                fontSize = 24.sp,
            )
        }
    }
}

@Composable
private fun SpacedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
) {
    val spacing = when(text.length) {
        2 -> 1.0.em
        3 -> 0.5.em
        4 -> 0.25.em
        else -> 0.0.em
    }

    Text(
        text = text,
        letterSpacing = spacing,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}