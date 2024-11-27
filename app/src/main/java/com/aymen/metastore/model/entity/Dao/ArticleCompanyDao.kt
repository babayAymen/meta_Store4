package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleCompanyRandomRKE
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleContainingRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.store.model.Enum.CompanyCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleCompanyDao {

    @Upsert
    suspend fun insertArticle(article: List<ArticleCompany>)

    @Transaction
    @Query("SELECT * FROM article_company WHERE isRandom = 1 ")
     fun getRandomArticles(): PagingSource<Int,ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT ac.* FROM article_company AS ac JOIN article AS a ON ac.articleId = a.id WHERE isRandom = 1 AND a.category = :categoryName")
     fun testGetRandomArticles(categoryName : CompanyCategory) : List<ArticleWithArticleCompany>
    @Transaction
    @Query("SELECT * FROM article_company WHERE id = :id")
    fun getArticleDetails(id : Long) : Flow<ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT ac.* FROM article_company AS ac JOIN article AS a ON ac.articleId = a.id WHERE" +
            " (a.libelle LIKE '%' || :libelle || '%') OR (a.code LIKE '%' || :libelle || '%') AND ac.companyId = :companyId")
     fun getAllMyArticleContaining(libelle : String, companyId: Long) : PagingSource<Int,ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT ac.* FROM article_company AS ac JOIN article AS a ON ac.articleId = a.id WHERE" +
            " (a.libelle LIKE '%' || :libelle || '%') OR (a.code LIKE '%' || :libelle || '%') AND ac.companyId != :companyId")
     fun getAllArticleContaining(libelle : String, companyId: Long) : PagingSource<Int,ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT * FROM Article_company WHERE companyId = :companyId")
    fun getAllMyArticles(companyId : Long): PagingSource<Int,ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT * FROM article_company WHERE companyId = :companyId")
     fun getAllArticlesByCompanyId(companyId : Long) : PagingSource<Int,ArticleWithArticleCompany>

    @Query("UPDATE article_company SET isFav = :isFave WHERE id = :articleId")
    suspend fun chageIsFav(articleId : Long , isFave : Boolean)

    @Transaction
    @Query(" SELECT ac.* FROM article_company AS ac JOIN company AS c ON ac.companyId = c.companyId WHERE c.category = :category")
    suspend fun getArticlesByCompanyCategory(category: CompanyCategory): List<ArticleWithArticleCompany>

    @Query("SELECT * FROM article_company WHERE categoryId = :categoryId AND companyId = :companyId")
    suspend fun getAllArticlesByCategoryIdAndCompanyId(categoryId : Long , companyId : Long) : List<ArticleCompany>

    @Query("SELECT * FROM article_company WHERE subCategoryId = :subCategoryId AND companyId = :companyId")
    suspend fun getAllArticlesBySubCategoryIdAndCompanyId(subCategoryId : Long , companyId : Long) : List<ArticleCompany>


    @Query("SELECT * FROM article_company WHERE id = :articleId")
    suspend fun getArticleCompanyById(articleId : Long) : ArticleCompany

    @Query("SELECT * FROM article_remote_keys_table WHERE id = :id")
    suspend fun getArticleRemoteKey(id : Long): ArticleRemoteKeysEntity

    @Query("SELECT * FROM article_company_random_remote_keys WHERE id = :id")
    suspend fun getArticleRandomRemoteKey(id : Long): ArticleCompanyRandomRKE

    @Query("SELECT * FROM article_containing_remote_keys WHERE id = :id")
    suspend fun getArticleContainingRemoteKey(id : Long) : ArticleContainingRemoteKeysEntity

    @Query("DELETE FROM article_remote_keys_table")
    suspend fun clearAllRemoteKeysTable()

    @Query("DELETE FROM article_company_random_remote_keys")
    suspend fun clearAllRandomRemoteKeysTable()

    @Query("DELETE FROM article_company WHERE isRandom = 1")
    suspend fun clearAllArticleCompanyTable()

    @Query("DELETE FROM article_containing_remote_keys")
    suspend fun clearAllArticleContainingRemoteKeysTable()

    @Upsert
    suspend fun insertKeys(articleRemoteKeysEntity: List<ArticleRemoteKeysEntity>)

    @Upsert
    suspend fun insertArticleRandomKeys(keys : List<ArticleCompanyRandomRKE>)

    @Upsert
    suspend fun insertArticleContainingKeys(keys : List<ArticleContainingRemoteKeysEntity>)
















}