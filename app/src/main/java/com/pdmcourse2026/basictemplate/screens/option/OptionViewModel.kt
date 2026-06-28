package com.pdmcourse2026.basictemplate.screens.option

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pdmcourse2026.basictemplate.RanKeucaApplication
import com.pdmcourse2026.basictemplate.data.model.Option
import com.pdmcourse2026.basictemplate.data.repository.QuestionOfflineFirstRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val repository: QuestionOfflineFirstRepository,
    private val questionId: Int
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val options: StateFlow<List<Option>> =
        repository.getOptions(questionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addOption(value: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.createOption(questionId, value)
            } catch (e: Exception) {
                _error.value = "No se pudo añadir la opción. Verifica tu conexión."
                Log.e("OptionsViewModel", "Error adding option", e)
            }
        }
    }

    fun updateOption(id: Int, value: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.updateOption(id, value)
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar la opción. Verifica tu conexión."
                Log.e("OptionsViewModel", "Error updating option", e)
            }
        }
    }

    fun deleteOption(option: Option) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.deleteOption(option.id)
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar la opción. Verifica tu conexión."
                Log.e("OptionsViewModel", "Error deleting option", e)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        fun provideFactory(questionId: Int) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RanKeucaApplication
                OptionsViewModel(app.appProvider.provideRepository(), questionId)
            }
        }
    }
}
