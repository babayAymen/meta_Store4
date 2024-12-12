package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.RandomArticle
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleCompanyRandomRKE
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleContainingRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.CompanyArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.RandomArticleChild
import com.aymen.store.model.Enum.CompanyCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleCompanyDao {

    @Upsert
    suspend fun insert(article: List<ArticleCompany>)

    suspend fun insertArticle(article: List<ArticleCompany?>){
        article.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }
    @Upsert
    suspend fun insertSigleArticle(article: ArticleCompany)

    @Query("SELECT MAX(id) FROM article_company")
    suspend fun getLatestArticleId(): Long?

    @Upsert
    suspend fun insertSingleKey(key : ArticleRemoteKeysEntity)

    @Upsert
    suspend fun insertCompanyArticleKeys(keys : List<CompanyArticleRemoteKeysEntity>)

    @Query("UPDATE article_company SET quantity = quantity + :quantity WHERE id = :articleId")
    suspend fun upDateQuantity(articleId : Long, quantity: Double)

    @Query("DELETE FROM article_company WHERE id = :id")
    suspend fun clearArticleById(id : Long)

    @Query("DELETE FROM article_remote_keys_table WHERE id = :id")
    suspend fun clearRemoteKeyById(id : Long)

    @Query("SELECT * FROM article_remote_keys_table WHERE id = :id")
    suspend fun getArticleRemoteKeysById(id : Long) : ArticleRemoteKeysEntity

    @Upsert
    suspend fun insertRandomArticle(article: List<RandomArticle>)

    @Transaction
    @Query("SELECT * FROM random_article_company  ")
     fun getRandomArticles(): PagingSource<Int, RandomArticleChild>

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
    @Query("SELECT * FROM Article_company WHERE companyId = :companyId ORDER BY id ASC")
    fun getAllMyArticles(companyId : Long): PagingSource<Int,ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT * FROM article_company WHERE companyId = :companyId")
     fun getAllArticlesByCompanyId(companyId : Long) : PagingSource<Int,ArticleWithArticleCompany>

    @Query("UPDATE article_company SET isFav = :isFave WHERE id = :articleId")
    suspend fun chageIsFav(articleId : Long , isFave : Boolean)


    @Query("SELECT * FROM article_company WHERE id = :articleId")
    suspend fun getArticleCompanyById(articleId : Long) : ArticleCompany

    @Transaction
    @Query("SELECT * FROM article_company WHERE id = :id")
    suspend fun getRandomArticle(id : Long) : ArticleWithArticleCompany

    @Query("SELECT * FROM article_remote_keys_table WHERE id = :id")
    suspend fun getArticleRemoteKey(id : Long): ArticleRemoteKeysEntity

    @Query("SELECT * FROM article_company WHERE isSync = :isSync")
    suspend fun getArticleCompanyByIsSync(isSync : Boolean) : ArticleCompany


    @Query("SELECT count(*) FROM article_company")
    suspend fun getArticlesCount():Int

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


    @Query("DELETE FROM random_article_company")
    suspend fun clearAllRandomArticleCompanyTable()

    @Query("DELETE FROM article_containing_remote_keys")
    suspend fun clearAllArticleContainingRemoteKeysTable()

    @Query("DELETE FROM article_company WHERE companyId = :companyId")
    suspend fun clearAllCompanyArticleTableById(companyId : Long)

    @Query("DELETE FROM company_article_remote_keys")
    suspend fun clearAllCompanyArticleRemoteKeysTable()

    @Upsert
    suspend fun insertKeys(articleRemoteKeysEntity: List<ArticleRemoteKeysEntity>)

    @Upsert
    suspend fun insertArticleRandomKeys(keys : List<ArticleCompanyRandomRKE>)

    @Upsert
    suspend fun insertArticleContainingKeys(keys : List<ArticleContainingRemoteKeysEntity>)

    @Query("SELECT * FROM company_article_remote_keys WHERE id = :id")
    suspend fun getCompanyArticleRemoteKey(id : Long) : CompanyArticleRemoteKeysEntity















}