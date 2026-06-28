package com.pdmcourse2026.basictemplate.data.repository

import com.pdmcourse2026.basictemplate.data.api.RankeUcaApi
import com.pdmcourse2026.basictemplate.data.database.dao.OptionDao
import com.pdmcourse2026.basictemplate.data.database.dao.QuestionDao
import com.pdmcourse2026.basictemplate.data.database.entity.OptionEntity
import com.pdmcourse2026.basictemplate.data.database.entity.QuestionEntity
import com.pdmcourse2026.basictemplate.data.database.entity.toModel
import com.pdmcourse2026.basictemplate.data.model.Option
import com.pdmcourse2026.basictemplate.data.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionOfflineFirstRepositoryImpl(
    private val questionDao: QuestionDao,
    private val optionDao: OptionDao,
    private val api: RankeUcaApi
) : QuestionOfflineFirstRepository {

    override fun getQuestions(): Flow<List<Question>> = 
        questionDao.getQuestionsWithOptions().map { list -> list.map { it.toModel() } }

    override fun getOptions(questionId: Int): Flow<List<Option>> =
        optionDao.getOptionsForQuestion(questionId).map { entities -> entities.map { it.toModel() } }

    override suspend fun refresh() {
        val remoteQuestions = api.fetchQuestions()
        val remoteOptions = api.fetchOptions()

        // 1. Eliminar datos locales que ya no existen en el servidor
        val remoteQuestionIds = remoteQuestions.map { it.id }
        val remoteOptionIds = remoteOptions.map { it.id }
        
        questionDao.deleteQuestionsNotIn(remoteQuestionIds)
        optionDao.deleteOptionsNotIn(remoteOptionIds)

        // 2. Sincronización de datos (Insert/Update)
        questionDao.insertQuestions(remoteQuestions.map { 
            QuestionEntity(id = it.id, title = it.title)
        })
        optionDao.insertOptions(remoteOptions.map { 
            OptionEntity(
                id = it.id,
                name = it.name,
                imageUrl = it.imageUrl,
                questionId = it.questionId,
                votes = it.votes ?: 0
            )
        })
    }

    override suspend fun createQuestion(text: String) {
        api.createQuestion(text)
        refresh()
    }

    override suspend fun updateQuestion(id: Int, text: String) {
        api.updateQuestion(id, text)
        refresh()
    }

    override suspend fun deleteQuestion(id: Int) {
        api.deleteQuestion(id)
        questionDao.deleteQuestionById(id)
    }

    override suspend fun createOption(questionId: Int, value: String) {
        api.createOption(questionId, value, null) // Pasamos null como imageUrl por defecto
        refresh()
    }

    override suspend fun updateOption(id: Int, value: String) {
        api.updateOption(id, value, null) // Pasamos null como imageUrl por defecto
        refresh()
    }

    override suspend fun deleteOption(id: Int) {
        api.deleteOption(id)
        optionDao.deleteOptionById(id)
    }
}
