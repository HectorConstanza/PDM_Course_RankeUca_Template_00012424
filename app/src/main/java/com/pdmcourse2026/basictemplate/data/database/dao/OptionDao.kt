package com.pdmcourse2026.basictemplate.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pdmcourse2026.basictemplate.data.database.entity.OptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OptionDao {

    @Query("SELECT * FROM options WHERE questionId = :questionId")
    fun getOptionsForQuestion(questionId: Int): Flow<List<OptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOption(option: OptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<OptionEntity>)

    @Delete
    suspend fun deleteOption(option: OptionEntity)

    @Query("DELETE FROM options WHERE id = :id")
    suspend fun deleteOptionById(id: Int)

    @Query("DELETE FROM options WHERE id NOT IN (:ids)")
    suspend fun deleteOptionsNotIn(ids: List<Int>)
}