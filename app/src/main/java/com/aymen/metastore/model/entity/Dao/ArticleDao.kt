package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.room.remoteKeys.ArtRemoteKeysEntity
import com.aymen.store.model.Enum.CompanyCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Upsert
    suspend fun insertArticle(article: List<Article>)

    @Upsert
    suspend fun insertKeys(keys : List<ArtRemoteKeysEntity>)

    @Query("SELECT * FROM art_remote_keys_entity WHERE id = :id")
    suspend fun getArticleRemoteKey(id : Long) : ArtRemoteKeysEntity

    @Query("SELECT * FROM article WHERE category = :companyCategory")
    fun getArticlesForCompanyByCompanyCategory( companyCategory: CompanyCategory) : PagingSource<Int,Article>

    @Query("DELETE FROM art_remote_keys_entity")
    suspend fun clearAllArticleRemoteKeys()

    @Query("DELETE FROM article")
    fun clearAllArticle()

    @Query("SELECT * FROM article WHERE category = :category")
     fun getAllArticlesByCategory(category : CompanyCategory): PagingSource<Int,Article>

    @Query("SELECT * FROM article WHERE id = :articleId")
    suspend fun getArticleById(articleId : Long) : Article
}