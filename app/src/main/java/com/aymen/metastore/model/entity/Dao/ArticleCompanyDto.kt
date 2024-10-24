package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.ArticleCompany

@Dao
interface ArticleCompanyDao {

    @Upsert
    suspend fun insertArticle(article: ArticleCompany)

    @Query("SELECT * FROM Article_company")
    suspend fun getAllArticles(): List<ArticleCompany>

    @Query("UPDATE article_company SET isFav = :isFave WHERE id = :articleId")
    suspend fun chageIsFav(articleId : Long , isFave : Boolean)

}