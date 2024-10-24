package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Upsert
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history")
    suspend fun getAllSearchHistories(): List<SearchHistory>
}