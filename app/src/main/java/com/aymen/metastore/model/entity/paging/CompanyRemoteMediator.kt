package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ClientProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
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
    private val categoryName : CompanyCategory?,
    private val searchType : SearchType?,
    private val libelle : String?
): RemoteMediator<Int, CompanyWithCompanyClient>() {


    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val companyClientRelationDao = room.clientProviderRelationDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CompanyWithCompanyClient>
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

                    userDao.insertUser(response.map {user -> user.person?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.client?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.client?.toCompany()!!})
                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()!!})
                    companyDao.insertCompany(response.map { company -> company.provider?.toCompany()!! })
                    companyClientRelationDao.insertClientProviderRelation(response.map { relation -> relation.toClientProviderRelation() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }
    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, CompanyWithCompanyClient>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { companyClientRelationDao.getRelationRemoteKey(it.relation.id!!).previousPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, CompanyWithCompanyClient>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { companyClientRelationDao.getRelationRemoteKey(it.relation.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, CompanyWithCompanyClient>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.relation?.id?.let { companyClientRelationDao.getRelationRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        companyClientRelationDao.clearAllRelationTable()
        companyClientRelationDao.clearAllRemoteKeysTable()
    }
}