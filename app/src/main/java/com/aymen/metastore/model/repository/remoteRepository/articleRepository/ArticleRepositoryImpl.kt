package com.aymen.metastore.model.repository.remoteRepository.articleRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.Transaction
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.paging.ArticleCompanyRandomMediator
import com.aymen.metastore.model.entity.paging.ArticleCompanyRemoteMediator
import com.aymen.metastore.model.entity.paging.ArticleContainingRemoteMediator
import com.aymen.metastore.model.entity.paging.ArticleRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.RandomArticleChild
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.Resource
import com.aymen.metastore.util.networkBoundResource
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor
    (       private val api: ServiceApi,
            private val sharedViewModel: SharedViewModel,
            private val room : AppDatabase
            )
    : ArticleRepository
{
    private val articleCompanyDao = room.articleCompanyDao()
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val articleDao = room.articleDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getRandomArticles(categoryName : CompanyCategory) :Flow<PagingData<RandomArticleChild>>{
            return Pager(
    config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 0),
    remoteMediator = ArticleCompanyRandomMediator(
    api = api, room = room
    ),
    pagingSourceFactory = {
        articleCompanyDao.getRandomArticles()
    }
    ).flow.map {
                it.map { article ->
                    article
                }
            }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyArticles(companyId: Long): Flow<PagingData<ArticleWithArticleCompany>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = ArticleCompanyRemoteMediator(
                api = api, room = room, id = companyId
            ),
            pagingSourceFactory = { articleCompanyDao.getAllMyArticles(companyId = companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyArticleContaining(libelle: String, searchType: SearchType, companyId : Long): Flow<PagingData<ArticleWithArticleCompany>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = ArticleContainingRemoteMediator(
                api = api, room = room,search = libelle, searchType = searchType
            ),
            pagingSourceFactory = { articleCompanyDao.getAllMyArticleContaining(libelle,companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
    @OptIn(ExperimentalPagingApi::class)
    override fun getAllArticlesByCategor(companyId : Long, companyCategory : CompanyCategory) :Flow<PagingData<Article>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 2),
            remoteMediator = ArticleRemoteMediator(
                api = api, room = room,  companyId = companyId
            ),
            pagingSourceFactory = { articleDao.getArticlesForCompanyByCompanyCategory( companyCategory = companyCategory)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    override suspend fun getArticleByBarcode(bareCode: String) = api.getArticleByBarcode(barCode = bareCode)

    override fun getArticleDetails(id: Long): Flow<Resource<ArticleCompany>> {
        return networkBoundResource(
            databaseQuery = {
                articleCompanyDao.getArticleDetails(id).map { it.toArticleRelation() }
             },
            apiCall = {
                api.getArticleDetails(id)
            },
            saveApiCallResult = {
                insert(it)
            }
        )
    }


    override suspend fun getRandomArticlesByCompanyCategory(categName : String) = api.getRandomArticlesByCompanyCategory(categName)
    override suspend fun getRandomArticlesByCategory(categoryId: Long, companyId : Long) = api.getRandomArticlesByCategory(categoryId, companyId)
    override suspend fun getRandomArticlesBySubCategory(
        subcategoryId: Long,
        companyId: Long
    ) = api.getRandomArticlesBySubCategory(subcategoryId, companyId)
    override suspend fun deleteArticle(id: String) =api.deleteArticle(id)
    override suspend fun addArticle(article: String, file: File): Response<Void> {
       return api.addArticle(article,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
        )
    }
    override suspend fun addArticleWithoutImage(article: String, articleId : Long) = api.addArticleWithoutImage(articleId,article)
    override suspend fun getAllArticlesContaining(search: String, searchType: SearchType) = api.getAllArticlesContaining(search,searchType)
    override suspend fun likeAnArticle(articleId: Long, isFav : Boolean) = api.likeAnArticle(articleId,isFav)
    override suspend fun sendComment(comment: String, articleId: Long) = api.sendComment(comment, articleId)
    override suspend fun getComments(articleId: Long): Response<List<CommentDto>> = api.getComments(articleId)
    override suspend fun addQuantityArticle(quantity: Double, articleId: Long): Response<ArticleCompanyDto> {
        try {
            articleCompanyDao.upDateQuantity(articleId, quantity)
        }catch (ex : Exception){
            Log.e("addQuantityArticle", "exception is : ${ex.message}")
        }
    val response = api.addQuantityArticle(quantity, articleId)
        if(response.isSuccessful){
            response.body()?.let {
                articleCompanyDao.insertSigleArticle( it.toArticleCompany(false))
            }

        }
        return response
    }
    override suspend fun updateArticle(article: ArticleCompanyDto): Response<ArticleCompanyDto> {
        try {
            articleCompanyDao.insertSigleArticle(article.toArticleCompany(false))
        } catch (e: Exception) {
            Log.e("articleCompany", "Error saving article: ${e.message}")
        }
        val response = api.updateArticle(article)
        if(response.isSuccessful){
            response.body()?.let { updatedArticle ->
                articleCompanyDao.insertSigleArticle(updatedArticle.toArticleCompany(false))
            }
        }
        return response
    }

    private suspend fun insert(articleDetails : List<ArticleCompanyDto>){
        userDao.insertUser(articleDetails.map {user -> user.company?.user?.toUser()!!})
        companyDao.insertCompany(articleDetails.map {company -> company.company?.toCompany()!!})
        userDao.insertUser(articleDetails.map {user -> user.provider?.user?.toUser()!!})
        companyDao.insertCompany(articleDetails.map { company -> company.provider?.toCompany()!! })
        categoryDao.insertCategory(articleDetails.map {category -> category.category?.toCategory()!! })
        subCategoryDao.insertSubCategory(articleDetails.map {subCategory -> subCategory.subCategory?.toSubCategory()!! })
        articleDao.insertArticle(articleDetails.map {article -> article.article?.toArticle(isMy = true)!! })
        articleCompanyDao.insertArticle(articleDetails.map {dto ->
            dto.toArticleCompany(false) })
    }
}