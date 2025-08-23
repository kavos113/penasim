package com.example.penasim.ui.command

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination

object BatterDestination : NavigationDestination {
    override val route: String = "natter"
    override val titleResId: Int = R.string.batter
}

@Composable
fun BatterScreen(
    modifier: Modifier = Modifier,
) {
    Column {
        Text("Batter Screen")
    }
}