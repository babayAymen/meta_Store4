package com.aymen.metastore.model.entity.dao

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
    @Upsert
    suspend fun insertSingleRemoteKey(keys : AllSearchRemoteKeysEntity)

    //maybe not used
    @Transaction
    @Query("SELECT * FROM search_history ORDER BY lastModifiedDate DESC")
     fun getAllSearchHistories(): PagingSource<Int ,SearchHistoryWithClientOrProviderOrUserOrArticle>

     @Query("DELETE FROM search_history WHERE id = :id")
     suspend fun deleteSearchHistoryById(id : Long)
     @Query("DELETE FROM all_search_remote_keys WHERE id = :id")
     suspend fun deleteRemoteKeyById(id : Long)
    @Transaction
    @Query("SELECT s.*, u.* FROM search_history AS s JOIN user AS u ON s.userId = u.id WHERE u.username LIKE '%' || :search || '%'")
     fun getAllUserSearchHistories(search : String): PagingSource<Int ,SearchHistoryWithClientOrProviderOrUserOrArticle>

    @Transaction
    @Query("SELECT s.* , c.* FROM search_history AS s JOIN company AS c ON s.companyId = c.companyId WHERE " +
            "c.name LIKE '%' || :search || '%' OR c.code LIKE '%' || :search || '%'")
     fun getAllCompaniesSearchHistories(search : String): PagingSource<Int ,SearchHistoryWithClientOrProviderOrUserOrArticle>

    @Transaction
    @Query("SELECT s.* , c.* ,r.* FROM search_history AS s JOIN company AS c ON s.companyId = c.companyId" +
            " JOIN client_provider_relation AS r ON s.clientRelationId = r.clientId WHERE " +
            "(c.name LIKE '%' || :search || '%' OR c.code LIKE '%' || :search || '%') AND r.providerId = :myProviderId")
     fun getAllCompaniesClientSearchHistories(search : String, myProviderId : Long): PagingSource<Int ,SearchHistoryWithClientOrProviderOrUserOrArticle>

    @Transaction
    @Query("SELECT s.* , c.* ,r.* FROM search_history AS s JOIN company AS c ON s.companyId = c.companyId" +
            " JOIN client_provider_relation AS r ON s.clientRelationId = r.providerId WHERE " +
            "(c.name LIKE '%' || :search || '%' OR c.code LIKE '%' || :search || '%') AND r.clientId = :myClientId")
     fun getAllCompaniesProviderSearchHistories(search : String, myClientId : Long): PagingSource<Int ,SearchHistoryWithClientOrProviderOrUserOrArticle>

     @Query("SELECT * FROM all_search_remote_keys WHERE id = :id")
     suspend fun getSearchHistoryRemoteKey(id : Long) : AllSearchRemoteKeysEntity

     @Query("DELETE FROM search_history")
     suspend fun clearAllSearchHistoryTable()

     @Query("DELETE FROM all_search_remote_keys")
     suspend fun clearAllSearchHistoryRemoteKeysTable()

     @Query("SELECT * FROM all_search_remote_keys ORDER BY id DESC LIMIT 1")
     suspend fun getLatestRemoteKey() : AllSearchRemoteKeysEntity?

     @Query("SELECT * FROM all_search_remote_keys ORDER BY id ASC LIMIT 1")
     suspend fun getFirstSearchRemoteKey() : AllSearchRemoteKeysEntity?

     @Query("SELECT COUNT(*) FROM all_search_remote_keys")
     suspend fun getRemoteKeysCount() : Int

}