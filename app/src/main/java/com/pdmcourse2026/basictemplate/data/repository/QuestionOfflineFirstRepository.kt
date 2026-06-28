package com.pdmcourse2026.basictemplate.data.repository

import com.pdmcourse2026.basictemplate.data.model.Option
import com.pdmcourse2026.basictemplate.data.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionOfflineFirstRepository {

    // Leer: de Room (fuente de verdad)
    fun getQuestions(): Flow<List<Question>>
    fun getOptions(questionId: Int): Flow<List<Option>>

    // Sincronizar: API -> Room
    suspend fun refresh()

    // Mutar: API -> luego refresh()
    suspend fun createQuestion(text: String)
    suspend fun updateQuestion(id: Int, text: String)
    suspend fun deleteQuestion(id: Int)

    suspend fun createOption(questionId: Int, value: String)
    suspend fun updateOption(id: Int, value: String)
    suspend fun deleteOption(id: Int)
}
