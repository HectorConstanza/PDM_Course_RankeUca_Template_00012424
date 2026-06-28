package com.pdmcourse2026.basictemplate.screens.question

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pdmcourse2026.basictemplate.RanKeucaApplication
import com.pdmcourse2026.basictemplate.data.model.Question
import com.pdmcourse2026.basictemplate.data.repository.QuestionOfflineFirstRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuestionViewModel(
    private val repository: QuestionOfflineFirstRepository,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val questions: StateFlow<List<Question>> =
        repository.getQuestions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _error.value = null
            _isRefreshing.value = true
            try {
                repository.refresh()
            } catch (e: Exception) {
                if (questions.value.isEmpty()) {
                    _error.value = "Hola corazon bello, fijate que hubo error y no tengo datos guardados. Por favor presiona reintentar."
                }
                Log.e("QuestionViewModel", "Error refreshing", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun addQuestion(text: String) {
        viewModelScope.launch {
            try {
                repository.createQuestion(text)
            } catch (e: Exception) {
                _error.value = "No se pudo crear la pregunta. Por favor, verifica tu conexión a internet."
                Log.e("QuestionViewModel", "Error adding", e)
            }
        }
    }

    fun updateQuestion(id: Int, text: String) {
        viewModelScope.launch {
            try {
                repository.updateQuestion(id, text)
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar la pregunta. Por favor, verifica tu conexión a internet."
                Log.e("QuestionViewModel", "Error updating", e)
            }
        }
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch {
            try {
                repository.deleteQuestion(question.id)
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar la pregunta. Por favor, verifica tu conexión a internet."
                Log.e("QuestionViewModel", "Error deleting", e)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RanKeucaApplication
                QuestionViewModel(app.appProvider.provideRepository())
            }
        }
    }
}
