package com.example.penasim.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.penasim.ui.calender.CalendarViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CalendarViewModel()
        }
    }
}