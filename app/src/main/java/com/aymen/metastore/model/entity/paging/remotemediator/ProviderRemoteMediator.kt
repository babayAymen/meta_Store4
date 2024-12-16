package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ProviderRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
): RemoteMediator<Int,  CompanyWithCompanyOrUser>() {
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val companyClientRelationDao = room.clientProviderRelationDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CompanyWithCompanyOrUser>
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

            val response = api.getAllMyProvider(id , currentPage , state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    companyClientRelationDao.insertProviderKeys(response.map { article ->
                        ProviderRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.map {user -> user.client?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.client?.toCompany()})
                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map { company -> company.provider?.toCompany()})
                    companyClientRelationDao.insertClientProviderRelation(response.map { relation -> relation.toClientProviderRelation() })

                } catch (ex: Exception) {
                    Log.e("errorprovider", "$ex")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("errorprovider", "$ex")
            MediatorResult.Error(ex)
        }
    }
    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, CompanyWithCompanyOrUser>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { companyClientRelationDao.getProviderRemoteKey(it.relation.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, CompanyWithCompanyOrUser>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { companyClientRelationDao.getProviderRemoteKey(it.relation.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, CompanyWithCompanyOrUser>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.relation?.id?.let { companyClientRelationDao.getProviderRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        companyClientRelationDao.clearAllProviderTable(id)
        companyClientRelationDao.clearProviderRemoteKeysTable()
    }
}