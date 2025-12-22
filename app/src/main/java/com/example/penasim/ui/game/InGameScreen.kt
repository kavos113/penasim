package com.example.penasim.ui.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.OrderPlayerItem

// activeNumber: 1-9
@Composable
private fun OrderList(
    fielders: List<DisplayFielder>,
    activeNumber: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        repeat(fielders.size) {
            OrderPlayerItem(
                player = fielders[it],
                modifier = Modifier,
                isActive = it + 1 == activeNumber
            )
        }
    }
}