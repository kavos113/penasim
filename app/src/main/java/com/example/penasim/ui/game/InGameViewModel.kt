package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InGameViewModel @Inject constructor(

): ViewModel() {
    private val _uiState = MutableStateFlow(InGameInfo())
    val uiState: StateFlow<InGameInfo> = _uiState.asStateFlow()
}