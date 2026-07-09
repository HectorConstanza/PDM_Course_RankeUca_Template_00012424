package com.pdmcourse2026.basictemplate.data.repository

import com.pdmcourse2026.basictemplate.data.database.entity.QuestionWithOptions
import kotlinx.coroutines.flow.Flow

interface MassVoteRepository {
    fun getQuestionsWithOptions(): Flow<List<QuestionWithOptions>>
    suspend fun submitVotes(optionIds: List<Int>)
}
