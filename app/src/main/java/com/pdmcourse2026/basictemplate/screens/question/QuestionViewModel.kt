package com.pdmcourse2026.basictemplate.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pdmcourse2026.basictemplate.RanKeucaApplication
import com.pdmcourse2026.basictemplate.data.model.Question
import com.pdmcourse2026.basictemplate.data.repository.QuestionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuestionViewModel(
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    val questions: StateFlow<List<Question>> =
        questionRepository.getQuestions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    fun addQuestion(title: String) {
        viewModelScope.launch {
            questionRepository.addQuestion(title)
        }
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch {
            questionRepository.deleteQuestion(question)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RanKeucaApplication
                QuestionViewModel(app.appProvider.provideQuestionRepository())
            }
        }
    }
}
