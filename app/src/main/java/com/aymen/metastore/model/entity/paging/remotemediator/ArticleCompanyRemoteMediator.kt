package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ArticleCompanyRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,

    ): RemoteMediator<Int, ArticleWithArticleCompany>() {


    private val articleCompanyDao = room.articleCompanyDao()
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val articleDao = room.articleDao()
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
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }
                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getAll(id,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    articleCompanyDao.insertKeys(response.map { article ->
                        ArticleRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            previousPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.company?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.company?.toCompany()!!})
                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()!!})
                    companyDao.insertCompany(response.map { company -> company.provider?.toCompany()!! })
                    categoryDao.insertCategory(response.map {category -> category.category?.toCategory(isCategory = false)!! })
                    subCategoryDao.insertSubCategory(response.map {subCategory -> subCategory.subCategory?.toSubCategory()!! })
                    articleDao.insertArticle(response.map {article -> article.article?.toArticle(isMy = true)!! })
                    articleCompanyDao.insertArticle(response.map { it.toArticleCompany(isRandom = false, isSearch = false) })

                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex.message}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, ArticleWithArticleCompany>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { articleCompanyDao.getArticleRemoteKey(it.articleCompany.id!!).previousPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, ArticleWithArticleCompany>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { articleCompanyDao.getArticleRemoteKey(it.articleCompany.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, ArticleWithArticleCompany>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.articleCompany?.id?.let { articleCompanyDao.getArticleRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        articleCompanyDao.clearAllArticleCompanyTable()
        articleCompanyDao.clearAllRemoteKeysTable()
    }
}
