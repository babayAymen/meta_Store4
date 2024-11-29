package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.SearchHistory
import com.aymen.metastore.model.entity.room.remoteKeys.AllSearchRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle

@Dao
interface SearchHistoryDao {

    @Upsert
    suspend fun insertSearchHistory(searchHistory: List<SearchHistory>)

    @Upsert
    suspend fun insertSearch(searchHistory: SearchHistory)

    @Upsert
    suspend fun insertAllSearchKeys(keys : List<AllSearchRemoteKeysEntity>)
    @Transaction
    @Query("SELECT * FROM search_history")
     fun getAllSearchHistories(): PagingSource<Int ,SearchHistoryWithClientOrProviderOrUserOrArticle>

     @Query("SELECT * FROM all_search_remote_keys WHERE id = :id")
     suspend fun getSearchHistoryRemoteKey(id : Long) : AllSearchRemoteKeysEntity

     @Query("DELETE FROM search_history")
     suspend fun clearAllSearchHistoryTable()

     @Query("DELETE FROM all_search_remote_keys")
     suspend fun clearAllSearchHistoryRemoteKeysTable()


}