package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CompanyArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class CompanyArticleRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val companyId : Long
): RemoteMediator<Int, ArticleWithArticleCompany>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleWithArticleCompany>
    ): MediatorResult {
        return try {

            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                     0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem()
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem()
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getAllCompanyArticles(companyId,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    articleCompanyDao.insertCompanyArticleKeys(response.content.map { article ->
                        CompanyArticleRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.company?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map { company -> company.provider?.toCompany() })
                    categoryDao.insertCategory(response.content.map {category -> category.category?.toCategory(isCategory = false)})
                    subCategoryDao.insertSubCategory(response.content.map {subCategory -> subCategory.subCategory?.toSubCategory(isSubcategory = false) })
                    articleDao.insertArticle(response.content.map {article -> article.article?.toArticle(isMy = true) })
                    articleCompanyDao.insertArticle(response.content.map { it.toArticleCompany(isRandom = false, isSearch = false) })

                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex.message}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("errorinvoice", ex.message.toString())
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return articleCompanyDao.getFirstAllCompanyArticleRemotyeKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return articleCompanyDao.getLatestAllCompanyArticleRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
          articleCompanyDao.clearAllCompanyArticleTableByIdAndIsRandom(companyId)
        articleCompanyDao.clearAllCompanyArticleRemoteKeysTable()
    }
}