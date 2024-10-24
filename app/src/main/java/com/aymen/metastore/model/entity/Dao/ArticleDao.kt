package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Article

@Dao
interface ArticleDao {

    @Upsert
    suspend fun insertArticle(article: Article) : Long

    @Query("SELECT * FROM article")
    suspend fun getAllArticles(): List<Article>

}