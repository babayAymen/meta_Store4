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
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class AllSearchRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long?
)
    : RemoteMediator<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
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
            val response = api.getAllHistory(id!!,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    searchHistoryDao.insertAllSearchKeys(response.content.map { article ->
                        AllSearchRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                            userDao.insertUser( response.content.map{article -> article.article?.company?.user?.toUser()})
                            companyDao.insertCompany(response.content.map{article -> article.article?.company?.toCompany()})
                            articleDao.insertArticle(response.content.map { article -> article.article?.article?.toArticle(isMy = true) })
                            articleCompanyDao.insertArticleForSearch(response.content.map { article -> article.article?.toArticleCompany(isRandom = true, isSearch = true) })
                            userDao.insertUser(response.content.map { company -> company.company?.user?.toUser()})
                            companyDao.insertCompany(response.content.map { company -> company.company?.toCompany() })
                            userDao.insertUser(response.content.map { user -> user.user?.toUser() })
                            searchHistoryDao.insertSearchHistory(response.content.map { search -> search.toSearchHistory() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return searchHistoryDao.getFirstSearchRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return searchHistoryDao.getLatestRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        searchHistoryDao.clearAllSearchHistoryTable()
        searchHistoryDao.clearAllSearchHistoryRemoteKeysTable()
    }
}