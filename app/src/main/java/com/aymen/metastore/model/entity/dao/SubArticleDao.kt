package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.SubArticle
import com.aymen.metastore.model.entity.room.remoteKeys.SubArticleRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.SubArticleWithArticles

@Dao
interface SubArticleDao {

    @Upsert
    suspend fun insertKeys(keys : List<SubArticleRemoteKeys>)
    @Upsert
    suspend fun insertSubArticle(subArticles : List<SubArticle>)
    @Query("SELECT * from sub_article_remote_key ORDER BY id ASC LIMIT 1")
    suspend fun getFirstSubArticleRemoteKey() : SubArticleRemoteKeys?
    @Query("SELECT * from sub_article_remote_key ORDER BY id DESC LIMIT 1")
    suspend fun getLatestSubArticleRemoteKey() : SubArticleRemoteKeys?
    @Query("DELETE FROM sub_article")
    suspend fun clearAllSubArticleTable()
    @Query("DELETE FROM sub_article_remote_key")
    suspend fun clearAllRemoteKeysTable()
    @Query("SELECT * FROM sub_article WHERE parentArticleId = :parentId")
    fun getArticlesChilds(parentId : Long) : PagingSource<Int,SubArticleWithArticles>
}