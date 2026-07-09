package com.pdmcourse2026.basictemplate.data.repository

import com.pdmcourse2026.basictemplate.data.api.RankeUcaApi
import com.pdmcourse2026.basictemplate.data.database.dao.QuestionDao
import com.pdmcourse2026.basictemplate.data.database.entity.QuestionWithOptions
import kotlinx.coroutines.flow.Flow

class MassVoteRepositoryImpl(
    private val questionDao: QuestionDao,
    private val api: RankeUcaApi
) : MassVoteRepository {
    override fun getQuestionsWithOptions(): Flow<List<QuestionWithOptions>> {
        return questionDao.getQuestionsWithOptions()
    }

    override suspend fun submitVotes(optionIds: List<Int>) {
        optionIds.forEach {optionId ->
            api.vote(optionId)
        }
    }
}
