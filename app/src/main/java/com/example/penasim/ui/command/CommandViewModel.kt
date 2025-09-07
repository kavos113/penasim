package com.example.penasim.ui.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Position
import com.example.penasim.domain.isStarting
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

            println("Loaded ${players.size} players for team ${team.name}")
            println("Loaded ${fielderAppointments.size} fielder appointments for team ${team.name}")
            println("Loaded ${pitcherAppointments.size} pitcher appointments for team ${team.name}")
        }
    }

    fun updateFielderAppointment(playerId: Int, position: Position, number: Int) {
        println("Updating fielder appointment for playerId: $playerId, position: $position, number: $number")

        val currentAppointments = _uiState.value.currentFielderAppointments.toMutableList()
        val currentPlayerAppointment = currentAppointments.find { it.playerId == playerId } ?: return

        val updatedAppointment = currentPlayerAppointment.copy(
            position = position,
            number = number
        )

        currentAppointments.removeIf { it.playerId == playerId }
        currentAppointments.add(updatedAppointment)

        _uiState.update { currentState ->
            currentState.copy(fielderAppointments = currentAppointments)
        }
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

        _uiState.update { currentState ->
            currentState.copy(pitcherAppointments = currentAppointments)
        }
    }

    fun save() {
        viewModelScope.launch {
            updateFielderAppointmentsUseCase.execute(_uiState.value.fielderAppointments)
            updatePitcherAppointmentsUseCase.execute(_uiState.value.pitcherAppointments)
        }
    }

    fun selectFielder(playerId: Int) {
        if (_uiState.value.selectedFielderId == null) {
            _uiState.update { currentState ->
                currentState.copy(selectedFielderId = playerId)
            }
        } else {
            val currentSelected = _uiState.value.selectedFielderId!!
            if (currentSelected == playerId) {
                _uiState.update { currentState ->
                    currentState.copy(selectedFielderId = null)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(selectedFielderId = null)
                }

                val currentAppointment = _uiState.value.currentFielderAppointments.find { it.playerId == currentSelected } ?: return
                val targetAppointment = _uiState.value.currentFielderAppointments.find { it.playerId == playerId } ?: return

                if (currentAppointment.isStarting() && targetAppointment.isStarting()) {
                    // positionは入れ替えない
                    updateFielderAppointment(
                        playerId = currentSelected,
                        position = currentAppointment.position,
                        number = targetAppointment.number
                    )

                    updateFielderAppointment(
                        playerId = playerId,
                        position = targetAppointment.position,
                        number = currentAppointment.number
                    )
                } else {
                    // positionも入れ替える
                    updateFielderAppointment(
                        playerId = currentSelected,
                        position = targetAppointment.position,
                        number = targetAppointment.number
                    )

                    updateFielderAppointment(
                        playerId = playerId,
                        position = currentAppointment.position,
                        number = currentAppointment.number
                    )
                }
            }
        }
    }

    fun selectPitcher(playerId: Int) {
        if (_uiState.value.selectedPitcherId == null) {
            _uiState.update { currentState ->
                currentState.copy(selectedPitcherId = playerId)
            }
        } else {
            val currentSelected = _uiState.value.selectedPitcherId!!
            if (currentSelected == playerId) {
                _uiState.update { currentState ->
                    currentState.copy(selectedPitcherId = null)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(selectedPitcherId = null)
                }

                val currentAppointment = _uiState.value.pitcherAppointments.find { it.playerId == currentSelected } ?: return
                val targetAppointment = _uiState.value.pitcherAppointments.find { it.playerId == playerId } ?: return

                updatePitcherAppointment(
                    playerId = currentSelected,
                    isMain = targetAppointment.isMain,
                    type = targetAppointment.type,
                    number = targetAppointment.number
                )

                updatePitcherAppointment(
                    playerId = playerId,
                    isMain = currentAppointment.isMain,
                    type = currentAppointment.type,
                    number = currentAppointment.number
                )
            }
        }
    }

    fun changeOrderType(orderType: OrderType) {
        _uiState.update { currentState ->
            currentState.copy(currentFielderOrderType = orderType)
        }
    }
}