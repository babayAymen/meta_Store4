package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Rating
import com.aymen.metastore.model.entity.room.remoteKeys.RatingRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.RatingWithRater

@Dao
interface RatingDao {

    @Upsert
    suspend fun insertRating(rating : List<Rating>)

    @Upsert
    suspend fun insertRatingRemoteKeys(keys : List<RatingRemoteKeys>)

    @Query("SELECT COUNT(*) FROM rating_remote_keys")
    suspend fun getRecordsCount() : Int
    @Query("SELECT * FROM rating_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getFirstRatingRemoteKey() : RatingRemoteKeys?
    @Query("SELECT * FROM rating_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getLatestRatingRemoteKey() : RatingRemoteKeys?
    @Query("DELETE FROM rating_remote_keys")
    suspend fun clearAllRatingRemoteKeysTable()
    @Query("DELETE FROM rating")
    suspend fun clearAllRatingTable()

    @Transaction
    @Query("SELECT * FROM rating WHERE rateeCompanyId = :rateeId ORDER BY lastModifiedDate DESC")
    fun getAllRateeRatingCompany(rateeId : Long) : PagingSource<Int , RatingWithRater>
    @Transaction
    @Query("SELECT * FROM rating WHERE rateeUserId = :rateeId ORDER BY lastModifiedDate DESC")
    fun getAllRateeRatingUser(rateeId : Long) : PagingSource<Int , RatingWithRater>
    @Query("DELETE FROM rating WHERE id = :id")
    suspend fun deleteRatingById(id : Long)
    @Query("DELETE FROM rating_remote_keys WHERE id = :id")
    suspend fun deleteRemoteKeyById(id : Long)
    @Query("SELECT * FROM rating WHERE articleId = :articleId")
    fun getArticleComments(articleId : Long) : PagingSource<Int , RatingWithRater>
}