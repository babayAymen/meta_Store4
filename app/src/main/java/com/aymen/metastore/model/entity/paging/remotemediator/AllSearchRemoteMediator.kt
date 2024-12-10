package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.AllSearchRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class AllSearchRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long?
)
    : RemoteMediator<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val searchHistoryDao = room.searchHistoryDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    getNextPageClosestToCurrentPosition(state)?.minus(1)?:0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    nextePage
                }
            }
            val response = api.getAllHistory(id!!,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    searchHistoryDao.insertAllSearchKeys(response.map { article ->
                        AllSearchRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })
                            userDao.insertUser( response.filter { article -> article.article != null}.map{article -> article.article?.company?.user?.toUser()})
                            companyDao.insertCompany(response.filter { article -> article.article != null}.map{article -> article.article?.company?.toCompany()})
                            categoryDao.insertCategory(response.filter { article -> article.article != null}.map { article -> article.article?.category?.toCategory()!! })
                            subCategoryDao.insertSubCategory(response.filter { article -> article.article != null}.map { article -> article.article?.subCategory?.toSubCategory()!! })
                            articleDao.insertArticle(response.filter { article -> article.article != null}.map { article -> article.article?.article?.toArticle(isMy = true)!! })
                            articleCompanyDao.insertArticle(response.filter { article -> article.article != null}.map { article -> article.article?.toArticleCompany(true)!! })
                            userDao.insertUser(response.filter { article -> article.company != null}.map { company -> company.user?.toUser()})
                            companyDao.insertCompany(response.filter { article -> article.company != null}.map { company -> company.company?.toCompany() })
                            userDao.insertUser(response.filter { article -> article.user != null}.map { user -> user.user?.toUser() })
                            searchHistoryDao.insertSearchHistory(response.map { search -> search.toSearchHistory() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { searchHistoryDao.getSearchHistoryRemoteKey(it.searchHistory.id!!) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey = entity?.let { searchHistoryDao.getSearchHistoryRemoteKey(it.searchHistory.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey = entity?.searchHistory?.id?.let { searchHistoryDao.getSearchHistoryRemoteKey(it) }
        return remoteKey?.nextPage
    }

    private suspend fun deleteCache(){
        searchHistoryDao.clearAllSearchHistoryTable()
        searchHistoryDao.clearAllSearchHistoryRemoteKeysTable()
    }
}