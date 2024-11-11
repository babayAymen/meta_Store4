package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.store.model.Enum.CompanyCategory

@Dao
interface ArticleCompanyDao {

    @Upsert
    suspend fun insertArticle(article: ArticleCompany)

    @Query("SELECT * FROM Article_company")
    suspend fun getAllArticles(): List<ArticleWithArticleCompany>

    @Query("SELECT * FROM article_company WHERE companyId = :companyId")
    suspend fun getAllArticlesByCompanyId(companyId : Long) : List<ArticleCompany>

    @Query("UPDATE article_company SET isFav = :isFave WHERE id = :articleId")
    suspend fun chageIsFav(articleId : Long , isFave : Boolean)

    @Query(" SELECT ac.* FROM article_company AS ac JOIN company AS c ON ac.companyId = c.id WHERE c.category = :category")
    suspend fun getArticlesByCompanyCategory(category: CompanyCategory): List<ArticleWithArticleCompany>

    @Query("SELECT * FROM article_company WHERE categoryId = :categoryId AND companyId = :companyId")
    suspend fun getAllArticlesByCategoryIdAndCompanyId(categoryId : Long , companyId : Long) : List<ArticleCompany>

    @Query("SELECT * FROM article_company WHERE subCategoryId = :subCategoryId AND companyId = :companyId")
    suspend fun getAllArticlesBySubCategoryIdAndCompanyId(subCategoryId : Long , companyId : Long) : List<ArticleCompany>

    @Query("SELECT ac.* FROM article_company AS ac JOIN article AS a ON ac.articleId = a.id WHERE a.libelle LIKE '%' || :search || '%' OR a.code LIKE '%' || :search || '%'")
    suspend fun getAllArticlesContaining(search : String) : List<ArticleCompany>

    @Query("SELECT * FROM article_company WHERE id = :articleId")
    suspend fun getArticleCompanyById(articleId : Long) : ArticleCompany
}