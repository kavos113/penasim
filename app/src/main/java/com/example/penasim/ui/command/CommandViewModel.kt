package com.example.penasim.ui.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants.TEAM_ID
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Position
import com.example.penasim.usecase.GetFielderAppointmentByTeamUseCase
import com.example.penasim.usecase.GetMainMembersByTeamUseCase
import com.example.penasim.usecase.GetPitcherAppointmentByTeamUseCase
import com.example.penasim.usecase.GetPlayerInfosByTeamUseCase
import com.example.penasim.usecase.GetTeamUseCase
import com.example.penasim.usecase.UpdateFielderAppointmentsUseCase
import com.example.penasim.usecase.UpdateMainMembersUseCase
import com.example.penasim.usecase.UpdatePitcherAppointmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommandViewModel @Inject constructor(
    private val getTeamUseCase: GetTeamUseCase,
    private val getPlayerInfosByTeamUseCase: GetPlayerInfosByTeamUseCase,
    private val getFielderAppointmentByTeamUseCase: GetFielderAppointmentByTeamUseCase,
    private val getPitcherAppointmentByTeamUseCase: GetPitcherAppointmentByTeamUseCase,
    private val updateFielderAppointmentsUseCase: UpdateFielderAppointmentsUseCase,
    private val updatePitcherAppointmentsUseCase: UpdatePitcherAppointmentsUseCase,
    private val getMainMembersByTeamUseCase: GetMainMembersByTeamUseCase,
    private val updateMainMembersUseCase: UpdateMainMembersUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(CommandUiState())
    val uiState: StateFlow<CommandUiState> = _uiState.asStateFlow()

    fun setTeamId(teamId: Int) {
        viewModelScope.launch {
            val team = getTeamUseCase.execute(teamId) ?: return@launch

            val players = getPlayerInfosByTeamUseCase.execute(team.id)

            val fielderAppointments = getFielderAppointmentByTeamUseCase.execute(team)
            val pitcherAppointments = getPitcherAppointmentByTeamUseCase.execute(team)

            val mainMembers = getMainMembersByTeamUseCase.execute(team.id)

            _uiState.update { currentState ->
                currentState.copy(
                    team = team,
                    players = players,
                    fielderAppointments = fielderAppointments,
                    pitcherAppointments = pitcherAppointments,
                    mainMembers = mainMembers,
                    mainViewSelectedFielderId = null,
                    selectedFielder = OrderType.entries.associateWith { null },
                    selectedPitcherId = null
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            val team = getTeamUseCase.execute(TEAM_ID) ?: return@launch

            val players = getPlayerInfosByTeamUseCase.execute(team.id)

            val fielderAppointments = getFielderAppointmentByTeamUseCase.execute(team)
            val pitcherAppointments = getPitcherAppointmentByTeamUseCase.execute(team)

            val mainMembers = getMainMembersByTeamUseCase.execute(team.id)

            _uiState.update { currentState ->
                currentState.copy(
                    team = team,
                    players = players,
                    fielderAppointments = fielderAppointments,
                    pitcherAppointments = pitcherAppointments,
                    mainMembers = mainMembers,
                )
            }

            println("Loaded ${players.size} players for team ${team.name}")
            println("Loaded ${fielderAppointments.size} fielder appointments for team ${team.name}")
            println("Loaded ${pitcherAppointments.size} pitcher appointments for team ${team.name}")
        }
    }

    fun updateMainFielder(playerId: Int, memberType: MemberType) {
        val currentMainMembers = _uiState.value.mainMembers.toMutableList()
        val currentPlayerMember = currentMainMembers.find { it.playerId == playerId } ?: return

        val updatedMember = currentPlayerMember.copy(
            memberType = memberType
        )

        currentMainMembers.removeIf { it.playerId == playerId }
        currentMainMembers.add(updatedMember)

        _uiState.update { currentState ->
            currentState.copy(mainMembers = currentMainMembers)
        }
    }

    fun updateFielderAppointment(playerId: Int, position: Position, number: Int, orderType: OrderType) {
        println("Updating fielder appointment for playerId: $playerId, position: $position, number: $number")

        val currentAppointments = _uiState.value.fielderAppointments.toMutableList()
        val currentPlayerAppointment = currentAppointments.find { it.playerId == playerId && it.orderType == orderType } ?: return

        val updatedAppointment = currentPlayerAppointment.copy(
            position = position,
            number = number
        )

        currentAppointments.removeIf { it.playerId == playerId && it.orderType == orderType }
        currentAppointments.add(updatedAppointment)

        _uiState.update { currentState ->
            currentState.copy(fielderAppointments = currentAppointments)
        }
    }

    fun updatePitcherAppointment(playerId: Int, type: PitcherType, number: Int) {
        val currentAppointments = _uiState.value.pitcherAppointments.toMutableList()
        val currentPlayerAppointment = currentAppointments.find { it.playerId == playerId } ?: return

        val updatedAppointment = currentPlayerAppointment.copy(
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
        viewModelScope.launch(Dispatchers.IO) {
            updateFielderAppointmentsUseCase.execute(_uiState.value.fielderAppointments)
            updatePitcherAppointmentsUseCase.execute(_uiState.value.pitcherAppointments)
            updateMainMembersUseCase.execute(_uiState.value.mainMembers)
        }
    }

    fun selectFielder(playerId: Int, orderType: OrderType) {
        if (_uiState.value.selectedFielder[orderType] == null) {
            _uiState.update { currentState ->
                currentState.copy(selectedFielder = currentState.selectedFielder.toMutableMap().apply { this[orderType] = playerId })
            }
        } else {
            val currentSelected = _uiState.value.selectedFielder[orderType]!!
            if (currentSelected == playerId) {
                _uiState.update { currentState ->
                    currentState.copy(selectedFielder = currentState.selectedFielder.toMutableMap().apply { this[orderType] = null } )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(selectedFielder = currentState.selectedFielder.toMutableMap().apply { this[orderType] = null } )
                }

                val currentAppointment = _uiState.value.fielderAppointments.find { it.playerId == currentSelected && it.orderType == orderType } ?: return
                val targetAppointment = _uiState.value.fielderAppointments.find { it.playerId == playerId && it.orderType == orderType } ?: return

                if (currentAppointment.position == Position.BENCH || targetAppointment.position == Position.BENCH) {
                    updateFielderAppointment(
                        playerId = currentSelected,
                        position = targetAppointment.position,
                        number = targetAppointment.number,
                        orderType = orderType
                    )

                    updateFielderAppointment(
                        playerId = playerId,
                        position = currentAppointment.position,
                        number = currentAppointment.number,
                        orderType = orderType
                    )
                } else {
                    updateFielderAppointment(
                        playerId = currentSelected,
                        position = currentAppointment.position,
                        number = targetAppointment.number,
                        orderType = orderType
                    )

                    updateFielderAppointment(
                        playerId = playerId,
                        position = targetAppointment.position,
                        number = currentAppointment.number,
                        orderType = orderType
                    )
                }
            }
        }
    }

    fun selectMainFielder(playerId: Int) {
        if (_uiState.value.mainViewSelectedFielderId == null) {
            _uiState.update { currentState ->
                currentState.copy(mainViewSelectedFielderId = playerId)
            }
        } else {
            val currentSelected = _uiState.value.mainViewSelectedFielderId!!
            if (currentSelected == playerId) {
                _uiState.update { currentState ->
                    currentState.copy(mainViewSelectedFielderId = null)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(mainViewSelectedFielderId = null)
                }

                val currentMember =
                    _uiState.value.mainMembers.find { it.playerId == currentSelected } ?: return
                val targetMember =
                    _uiState.value.mainMembers.find { it.playerId == playerId } ?: return

                updateMainFielder(
                    playerId = currentSelected,
                    memberType = targetMember.memberType
                )

                updateMainFielder(
                    playerId = playerId,
                    memberType = currentMember.memberType
                )

                // update appointments
                for (orderType in OrderType.entries) {
                    val currentAppointment =
                        _uiState.value.fielderAppointments.find { it.playerId == currentSelected && it.orderType == orderType }
                    val targetAppointment =
                        _uiState.value.fielderAppointments.find { it.playerId == playerId && it.orderType == orderType }

                    if (currentAppointment != null && targetAppointment != null) {
                        updateFielderAppointment(
                            playerId = currentSelected,
                            position = targetAppointment.position,
                            number = targetAppointment.number,
                            orderType = orderType
                        )

                        updateFielderAppointment(
                            playerId = playerId,
                            position = currentAppointment.position,
                            number = currentAppointment.number,
                            orderType = orderType
                        )
                    }
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
                    type = targetAppointment.type,
                    number = targetAppointment.number
                )

                updatePitcherAppointment(
                    playerId = playerId,
                    type = currentAppointment.type,
                    number = currentAppointment.number
                )
            }
        }
    }
}