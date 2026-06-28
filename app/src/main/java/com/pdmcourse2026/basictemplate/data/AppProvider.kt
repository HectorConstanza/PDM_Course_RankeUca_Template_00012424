package com.pdmcourse2026.basictemplate.data

import android.content.Context
import com.pdmcourse2026.basictemplate.data.api.RankeUcaApi
import com.pdmcourse2026.basictemplate.data.database.AppDatabase
import com.pdmcourse2026.basictemplate.data.repository.QuestionOfflineFirstRepository
import com.pdmcourse2026.basictemplate.data.repository.QuestionOfflineFirstRepositoryImpl

class AppProvider(context: Context) {

    private val appDatabase = AppDatabase.getDatabase(context)
    private val optionDao = appDatabase.optionDao()
    private val questionDao = appDatabase.questionDao()
    
    private val api = RankeUcaApi()

    private val repository: QuestionOfflineFirstRepository =
        QuestionOfflineFirstRepositoryImpl(questionDao, optionDao, api)

    fun provideRepository(): QuestionOfflineFirstRepository = repository
}
