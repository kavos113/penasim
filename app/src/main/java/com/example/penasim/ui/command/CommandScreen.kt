package com.example.penasim.ui.command

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination

object CommandDestination : NavigationDestination {
    override val route: String = "command"
    override val titleResId: Int = R.string.command
}

@Composable
fun CommandScreen(modifier: Modifier = Modifier) {

}