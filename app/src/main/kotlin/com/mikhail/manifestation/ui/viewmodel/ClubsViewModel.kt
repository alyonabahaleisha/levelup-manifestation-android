package com.mikhail.manifestation.ui.viewmodel

// iOS Migration: -> ClubsViewModel.swift — @HiltViewModel -> @Observable, StateFlow -> @Published, viewModelScope.launch -> Task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.content.ClubRepository
import com.mikhail.manifestation.data.model.Club
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ClubsUiState {
    data object Loading : ClubsUiState
    data class Success(
        val clubs: List<Club>,
        val masterTelegramUrl: String
    ) : ClubsUiState
    data class Error(val message: String) : ClubsUiState
}

@HiltViewModel
class ClubsViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClubsUiState>(ClubsUiState.Loading)
    val uiState: StateFlow<ClubsUiState> = _uiState

    init {
        loadClubs()
    }

    fun loadClubs() {
        viewModelScope.launch {
            _uiState.value = ClubsUiState.Loading
            try {
                val clubs = clubRepository.getClubs()
                _uiState.value = ClubsUiState.Success(
                    clubs = clubs,
                    masterTelegramUrl = Translations.ui("clubs_master_telegram_url")
                )
            } catch (_: Exception) {
                _uiState.value = ClubsUiState.Error("Не удалось загрузить клубы")
            }
        }
    }
}
