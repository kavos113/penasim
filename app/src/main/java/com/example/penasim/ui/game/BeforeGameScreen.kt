package com.example.penasim.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination

object GameDestination : NavigationDestination {
    override val route: String = "game"
    override val titleResId: Int = R.string.game
}

@Composable
fun BeforeGameScreen(modifier: Modifier = Modifier) {

}