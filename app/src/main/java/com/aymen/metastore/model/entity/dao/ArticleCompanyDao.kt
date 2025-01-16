package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Comment
import com.aymen.metastore.model.entity.room.entity.RandomArticle
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleCompanyRandomRKE
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleContainingRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.CompanyArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.CommentWithArticleAndUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.RandomArticleChild
import com.aymen.store.model.Enum.CompanyCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleCompanyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(article: List<ArticleCompany>) : List<Long>

    suspend fun insertArticle(article: List<ArticleCompany?>){
       article.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }
    @Transaction
    suspend fun insertOrUpdateMy(articles: List<ArticleCompany>) {
        articles.forEach { article ->
            val result = insert(listOf(article)).firstOrNull() ?: -1L
            if (result == -1L) { // Conflict occurred
                val existingArticle = getArticleById(article.id!!) // Fetch the existing record

                if (existingArticle != null && article.isMy != existingArticle.isMy) {
                    updateMy(article.id, article.isMy!!) // Update only if price differs
                }
            }
        }
    }

    suspend fun insertOrUpdateIsSearch(articles : List<ArticleCompany>){
        articles.forEach { article ->
            val result = insert(listOf(article)).firstOrNull() ?: -1L
            if(result == -1L){
                val existsArticle = getArticleById(article.id!!)
                if(existsArticle != null && existsArticle.isSearch != article.isSearch){
                    updateForSearch(article.id , true)
                }
            }

        }
    }

    @Query("UPDATE article_company SET isSearch = :isSearch WHERE id = :id")
    suspend fun updateForSearch(id : Long , isSearch : Boolean)

    @Query("UPDATE article_company SET isMy = :isMy WHERE id = :id")
    suspend fun updateMy(id : Long , isMy : Boolean)
    @Upsert
    suspend fun insertForSearch(article: List<ArticleCompany>)

    suspend fun insertArticleForSearch(article: List<ArticleCompany?>){
        article.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insertOrUpdate(it)
            }
    }

    @Transaction
    suspend fun insertOrUpdate(articles: List<ArticleCompany>) {
        articles.forEach { article ->
            val result = insert(listOf(article)).firstOrNull() ?: -1L
            if (result == -1L) { // Conflict occurred
                val existingArticle = getArticleById(article.id!!) // Fetch the existing record
                if(existingArticle != null && article.isFav != existingArticle.isFav){
                    updateIsFav(article.id , isFav = article.isFav!!)
                }
                if (existingArticle != null && article.isRandom != existingArticle.isRandom) {
                    update(article.id, article.isRandom!!) // Update only if price differs
                }
            }
        }
    }
    @Query("UPDATE article_company SET isFav = :isFav WHERE id = :id")
    suspend fun updateIsFav(id : Long , isFav : Boolean)
    @Query("UPDATE article_company SET isRandom = :isRandom WHERE id = :id")
    suspend fun update(id : Long, isRandom : Boolean)

    @Query("SELECT * FROM article_company WHERE id = :id")
    suspend fun getArticleById(id : Long) : ArticleCompany?


    @Upsert
    suspend fun insertSigleArticle(article: ArticleCompany)

    @Transaction
    @Query("SELECT * FROM comment WHERE articleId = :articleId")
     fun getArticleComments(articleId : Long): PagingSource<Int, CommentWithArticleAndUserOrCompany>

    @Query("SELECT id FROM article_company ORDER BY id DESC LIMIT 1")
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
    suspend fun clearArticleCompanyRemoteKeyById(id : Long)
    @Query("DELETE FROM article_remote_keys_table WHERE id = :id")
    suspend fun clearRemoteKeyById(id : Long)

    @Query("SELECT * FROM article_remote_keys_table WHERE id = :id")
    suspend fun getArticleRemoteKeysById(id : Long) : ArticleRemoteKeysEntity

    @Upsert
    suspend fun insertRandomArticle(article: List<RandomArticle>)

    @Transaction
    @Query("SELECT r.* FROM article_company AS r JOIN article AS a ON r.articleId = a.id WHERE a.category = :category AND isRandom = 1")
     fun getRandomArticles(category : CompanyCategory): PagingSource<Int, ArticleWithArticleCompany>

     @Transaction
     @Query("SELECT * FROM article_company WHERE isRandom = 1 ORDER BY id DESC")
     fun getRandomArticlesWithouCategory(): PagingSource<Int, ArticleWithArticleCompany>
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
    @Query("SELECT * FROM article_company WHERE companyId = :companyId ORDER BY id DESC")
    fun getAllMyArticles(companyId : Long): PagingSource<Int,ArticleWithArticleCompany>

    @Transaction
    @Query("SELECT * FROM article_company WHERE companyId = :companyId")
     fun getAllArticlesByCompanyId(companyId : Long) : PagingSource<Int,ArticleWithArticleCompany>

    @Query("UPDATE article_company SET isFav = :isFave , likeNumber = likeNumber + CASE WHEN :isFave THEN 1 ELSE -1 END WHERE id = :articleId")
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

    @Query("DELETE FROM article_company_random_remote_keys WHERE id = :id")
    suspend fun clearRandomRemoteKeysTableById(id : Long)

    @Query("DELETE FROM article_company WHERE isRandom = 0 AND isSearch = 0 AND companyId = :companyId")
    suspend fun clearAllArticleCompanyTable(companyId: Long)


    @Query("DELETE FROM article_company " +
            "WHERE companyId IN (" +
            "    SELECT c.companyId " +
            "    FROM company AS c" +
            "    WHERE c.category = :category" +
            ") AND isRandom = 1 AND isSearch = 0 AND isMy = 0")
    suspend fun clearAllRandomArticleCompanyTable(category: CompanyCategory)

    @Query("SELECT ac.id FROM article_company AS ac JOIN company AS c ON ac.companyId = c.companyId WHERE c.category = :category")
    suspend fun getAllIdsByCategory(category: CompanyCategory) : List<Long>
    @Query("DELETE FROM article_containing_remote_keys")
    suspend fun clearAllArticleContainingRemoteKeysTable()

    @Query("DELETE FROM article_company WHERE companyId = :companyId AND isRandom = 0 AND isSearch = 0")
    suspend fun clearAllCompanyArticleTableByIdAndIsRandom(companyId : Long)

    @Query("DELETE FROM company_article_remote_keys")
    suspend fun clearAllCompanyArticleRemoteKeysTable()
    @Query("SELECT * FROM company_article_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getFirstAllCompanyArticleRemotyeKey() : CompanyArticleRemoteKeysEntity?
    @Query("SELECT * FROM company_article_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAllCompanyArticleRemoteKey() : CompanyArticleRemoteKeysEntity?
    @Upsert
    suspend fun insertKeys(articleRemoteKeysEntity: List<ArticleRemoteKeysEntity>)

    @Upsert
    suspend fun insertArticleRandomKeys(keys : List<ArticleCompanyRandomRKE>)

    @Upsert
    suspend fun insertArticleContainingKeys(keys : List<ArticleContainingRemoteKeysEntity>)

    @Query("SELECT * FROM company_article_remote_keys WHERE id = :id")
    suspend fun getCompanyArticleRemoteKey(id : Long) : CompanyArticleRemoteKeysEntity

    @Query("SELECT * FROM article_remote_keys_table ORDER BY id DESC LIMIT 1")
    suspend fun getFirstArticleCompanyRemoteKey() : ArticleRemoteKeysEntity?
    @Query("SELECT * FROM article_remote_keys_table ORDER BY id ASC LIMIT 1")
    suspend fun getLatestArticleCompanyRemoteKey() : ArticleRemoteKeysEntity?

    @Query("SELECT * FROM article_company_random_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getFirstRandomArticleCompanyRemoteKey() : ArticleCompanyRandomRKE?

    @Query("SELECT * FROM article_company_random_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getLatestRandomArticeCompanyRemoteKey() : ArticleCompanyRandomRKE?













}