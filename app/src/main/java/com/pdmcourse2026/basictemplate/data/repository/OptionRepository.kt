package com.pdmcourse2026.basictemplate.data.repository

import com.pdmcourse2026.basictemplate.data.model.Option
import kotlinx.coroutines.flow.Flow

interface OptionRepository {
    fun getOptions(): Flow<List<Option>>
    suspend fun addOption(option: Option)
    suspend fun deleteOption(option: Option)
}