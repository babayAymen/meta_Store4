package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Article
import com.aymen.store.model.Enum.CompanyCategory

@Dao
interface ArticleDao {

    @Upsert
    suspend fun insertArticle(article: Article) : Long

    @Query("SELECT * FROM article WHERE category = :category")
    suspend fun getAllArticlesByCategory(category : CompanyCategory): List<Article>

    @Query("SELECT * FROM article WHERE id = :articleId")
    suspend fun getArticleById(articleId : Long) : Article
}