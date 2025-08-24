package com.example.penasim.ui.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Position
import com.example.penasim.usecase.GetFielderAppointmentByTeamUseCase
import com.example.penasim.usecase.GetPitcherAppointmentByTeamUseCase
import com.example.penasim.usecase.GetPlayerInfosByTeamUseCase
import com.example.penasim.usecase.GetTeamUseCase
import com.example.penasim.usecase.UpdateFielderAppointmentsUseCase
import com.example.penasim.usecase.UpdatePitcherAppointmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TEAM_ID = 0 // Example team ID, replace with actual logic to get the team ID

@HiltViewModel
class CommandViewModel @Inject constructor(
    private val getTeamUseCase: GetTeamUseCase,
    private val getPlayerInfosByTeamUseCase: GetPlayerInfosByTeamUseCase,
    private val getFielderAppointmentByTeamUseCase: GetFielderAppointmentByTeamUseCase,
    private val getPitcherAppointmentByTeamUseCase: GetPitcherAppointmentByTeamUseCase,
    private val updateFielderAppointmentsUseCase: UpdateFielderAppointmentsUseCase,
    private val updatePitcherAppointmentsUseCase: UpdatePitcherAppointmentsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(CommandUiState())
    val uiState: StateFlow<CommandUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val team = getTeamUseCase.execute(TEAM_ID) ?: return@launch

            val players = getPlayerInfosByTeamUseCase.execute(team.id)

            val fielderAppointments = getFielderAppointmentByTeamUseCase.execute(team)
            val pitcherAppointments = getPitcherAppointmentByTeamUseCase.execute(team)

            _uiState.update { currentState ->
                currentState.copy(
                    team = team,
                    players = players,
                    fielderAppointments = fielderAppointments,
                    pitcherAppointments = pitcherAppointments
                )
            }
        }
    }

    fun updateFielderAppointment(playerId: Int, position: Position, isMain: Boolean, number: Int) {
        val currentAppointments = _uiState.value.fielderAppointments.toMutableList()
        val currentPlayerAppointment = currentAppointments.find { it.playerId == playerId } ?: return

        val updatedAppointment = currentPlayerAppointment.copy(
            position = position,
            isMain = isMain,
            number = number
        )

        currentAppointments.removeIf { it.playerId == playerId }
        currentAppointments.add(updatedAppointment)
    }

    fun updatePitcherAppointment(playerId: Int, isMain: Boolean, type: PitcherType, number: Int) {
        val currentAppointments = _uiState.value.pitcherAppointments.toMutableList()
        val currentPlayerAppointment = currentAppointments.find { it.playerId == playerId } ?: return

        val updatedAppointment = currentPlayerAppointment.copy(
            isMain = isMain,
            type = type,
            number = number
        )

        currentAppointments.removeIf { it.playerId == playerId }
        currentAppointments.add(updatedAppointment)
    }

    fun save() {
        viewModelScope.launch {
            updateFielderAppointmentsUseCase.execute(_uiState.value.fielderAppointments)
            updatePitcherAppointmentsUseCase.execute(_uiState.value.pitcherAppointments)
        }
    }
}