package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.SearchHistory
import com.aymen.metastore.model.entity.room.remoteKeys.ClientProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class CompanyRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val type : com.aymen.metastore.model.Enum.LoadType,
    private val id : Long? = null,
    private val searchType : SearchType?,
    private val libelle : String?
): RemoteMediator<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>() {


    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val companyClientRelationDao = room.clientProviderRelationDao()
    private val searchHistoryDao = room.searchHistoryDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>
    ):  MediatorResult {
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
            val response = when(type){
                com.aymen.metastore.model.Enum.LoadType.RANDOM -> {
                    api.getAllMyClientsContaining(id!!,searchType!!, libelle!!,currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.ADMIN -> {
                    api.getAllMyClientsContaining(id!!,searchType!!, libelle!!,currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.CONTAINING -> {
                    api.getAllCompaniesContaining(id!!, libelle!! , searchType!!,page = currentPage, pageSize = PAGE_SIZE)
                }
            }
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    companyClientRelationDao.insertKeys(response.map { article ->
                        ClientProviderRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            previousPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.map {user -> user.client?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.client?.toCompany()})
                    companyClientRelationDao.insertClientProviderRelation(response.map { relation -> relation.toClientProviderRelation() })
                    response.map { search ->
                        val entity = SearchHistory(
                            id = search.id,
                            userId = search.person?.id,
                            companyId = search.client?.id,
                            userRelationId = search.person?.id,
                            clientRelationId = search.client?.id
                        )
                        searchHistoryDao.insertSearch(entity)
                    }

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }
    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKeys = entity?.let { searchHistoryDao.getSearchHistoryRemoteKey(it.company?.company?.companyId?:it.user?.id!!) }
        return remoteKeys?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKeys =  entity?.let { searchHistoryDao.getSearchHistoryRemoteKey(it.company?.company?.companyId?:it.user?.id!!) }
        return remoteKeys?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, SearchHistoryWithClientOrProviderOrUserOrArticle>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKeys =  entity?.let { searchHistoryDao.getSearchHistoryRemoteKey(it.company?.company?.companyId?:it.user?.id!!) }
        return remoteKeys?.nextPage
    }

    private suspend fun deleteCache(){
      //  searchHistoryDao.clearAllSearchHistoryTable()
        searchHistoryDao.clearAllSearchHistoryRemoteKeysTable()
    }
}