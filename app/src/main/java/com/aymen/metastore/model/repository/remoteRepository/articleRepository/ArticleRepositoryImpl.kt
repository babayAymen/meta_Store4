package com.aymen.metastore.model.repository.remoteRepository.articleRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.paging.pagingsource.AllArticlesContainingPagingSource
import com.aymen.metastore.model.entity.paging.remotemediator.ArticleCompanyRandomMediator
import com.aymen.metastore.model.entity.paging.remotemediator.ArticleCompanyRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.ArticleRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.CompanyArticleRemoteMediator
import com.aymen.metastore.model.entity.paging.pagingsource.CompanyArticlesByCategoryOrSubCategoryPagingSource
import com.aymen.metastore.model.entity.paging.remotemediator.CommentArticleRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.CommentWithArticleAndUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.RandomArticleChild
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.metastore.util.Resource
import com.aymen.metastore.util.networkBoundResource
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
@OptIn(ExperimentalPagingApi::class)
class ArticleRepositoryImpl @Inject constructor
    (private val api: ServiceApi,
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

    override fun getRandomArticles(categoryName : CompanyCategory) :Flow<PagingData<RandomArticleChild>>{
            return Pager(
    config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
    remoteMediator = ArticleCompanyRandomMediator(
    api = api, room = room, category = categoryName
    ),
    pagingSourceFactory = {
        articleCompanyDao.getRandomArticles(categoryName)
    }
    ).flow.map {
                it.map { article ->
                    article
                }
            }
    }

    override fun getAllMyArticles(companyId: Long): Flow<PagingData<ArticleWithArticleCompany>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
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

    override fun getAllMyArticleContaining(libelle: String, searchType: SearchType, companyId : Long): Flow<PagingData<ArticleCompanyDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Number of items per page
                enablePlaceholders = false // Disable placeholders for unloaded pages
            ),
            pagingSourceFactory = {
                AllArticlesContainingPagingSource(api, libelle, searchType,companyId)
            }
        ).flow
    }
    override fun getAllArticlesByCategor(companyId : Long, companyCategory : CompanyCategory) :Flow<PagingData<Article>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
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

    override fun getAllCompanyArticles(companyId: Long): Flow<PagingData<ArticleWithArticleCompany>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = CompanyArticleRemoteMediator(
                api = api, room = room,  companyId = companyId
            ),
            pagingSourceFactory = { articleCompanyDao.getAllMyArticles(companyId = companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    override fun getArticlesByCompanyAndCategoryOrSubCategory(
        companyId: Long,
        categoryId: Long,
        subcategoryId: Long
    ): Flow<PagingData<ArticleCompanyDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Number of items per page
                enablePlaceholders = false // Disable placeholders for unloaded pages
            ),
            pagingSourceFactory = {
                CompanyArticlesByCategoryOrSubCategoryPagingSource(api, companyId, categoryId,subcategoryId)
            }
        ).flow
    }

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
    override suspend fun deleteArticle(id: Long) =api.deleteArticle(id)
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
    override suspend fun addArticleWithoutImage(article: ArticleCompanyDto, articleId: Long): Response<ArticleCompanyDto> = api.addArticleWithoutImage(articleId,article)
    override suspend fun getAllArticlesContaining(search: String, searchType: SearchType) = api.getAllArticlesContaining(search,searchType)
    override suspend fun likeAnArticle(articleId: Long, isFav : Boolean) = api.likeAnArticle(articleId,isFav)
    override suspend fun sendComment(comment: CommentDto) = api.sendComment(comment)
    override fun getArticleComments(articleId: Long): Flow<PagingData<CommentWithArticleAndUserOrCompany>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = CommentArticleRemoteMediator(
                api = api, room = room,  articleId = articleId
            ),
            pagingSourceFactory = { articleCompanyDao.getArticleComments(articleId = articleId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
    override suspend fun addQuantityArticle(quantity: Double, articleId: Long): Response<ArticleCompanyDto> = api.addQuantityArticle(quantity, articleId)

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
        categoryDao.insertCategory(articleDetails.map {category -> category.category?.toCategory(isCategory = false)!! })
        subCategoryDao.insertSubCategory(articleDetails.map {subCategory -> subCategory.subCategory?.toSubCategory()!! })
        articleDao.insertArticle(articleDetails.map {article -> article.article?.toArticle(isMy = true)!! })
        articleCompanyDao.insertArticle(articleDetails.map {dto ->
            dto.toArticleCompany(false) })
    }
}