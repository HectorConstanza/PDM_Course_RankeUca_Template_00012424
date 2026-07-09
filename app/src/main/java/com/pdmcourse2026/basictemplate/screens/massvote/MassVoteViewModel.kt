package com.pdmcourse2026.basictemplate.screens.massvote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pdmcourse2026.basictemplate.RanKeucaApplication
import com.pdmcourse2026.basictemplate.data.database.entity.QuestionWithOptions
import com.pdmcourse2026.basictemplate.data.repository.MassVoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MassVoteViewModel(
    private val repository: MassVoteRepository
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val questions: StateFlow<List<QuestionWithOptions>> =
        repository.getQuestionsWithOptions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun submitVotes(selectedOptions: Map<Int, Int>) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null
            try {
                repository.submitVotes(selectedOptions.values.toList())
            } catch (e: Exception) {
                _error.value = "Error al enviar los votos. Intenta de nuevo."
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RanKeucaApplication
                MassVoteViewModel(app.appProvider.provideMassVoteRepository())
            }
        }
    }
}
