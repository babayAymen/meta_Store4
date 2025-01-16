package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.room.remoteKeys.ArtRemoteKeysEntity
import com.aymen.store.model.Enum.CompanyCategory

@Dao
interface ArticleDao {

    @Upsert
    suspend fun insert(article: List<Article>)

    suspend fun insertArticle(article: List<Article?>){
        article.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }
    @Upsert
    suspend fun insertKeys(keys : List<ArtRemoteKeysEntity>)

    @Query("SELECT * FROM art_remote_keys_entity WHERE id = :id")
    suspend fun getArticleRemoteKey(id : Long) : ArtRemoteKeysEntity

    @Query("SELECT * FROM article WHERE category = :companyCategory AND isMy = 0")
    fun getArticlesForCompanyByCompanyCategory( companyCategory: CompanyCategory) : PagingSource<Int,Article>

    @Query("DELETE FROM art_remote_keys_entity")
    suspend fun clearAllArticleRemoteKeys()

    @Query("DELETE FROM article WHERE isMy = :isMy")
    fun clearAllArticle(isMy : Boolean)

    @Query("SELECT * FROM article WHERE category = :category")
     fun getAllArticlesByCategory(category : CompanyCategory): PagingSource<Int,Article>

    @Query("SELECT * FROM article WHERE id = :articleId")
    suspend fun getArticleById(articleId : Long) : Article

    @Query("UPDATE article SET isMy = :isMy WHERE id = :id")
    suspend fun updateArticleById(isMy : Boolean, id : Long)

    @Query("SELECT * FROM art_remote_keys_entity ORDER BY id ASC LIMIT 1")
    suspend fun getFirstArticleRemoteKey() : ArtRemoteKeysEntity?
    @Query("SELECT * FROM art_remote_keys_entity ORDER BY id DESC LIMIT 1")
    suspend fun getLatestArticleRemoteKey() : ArtRemoteKeysEntity?

}












