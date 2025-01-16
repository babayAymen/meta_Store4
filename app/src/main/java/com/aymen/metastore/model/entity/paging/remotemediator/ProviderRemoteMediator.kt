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

            val response = api.getAllMyProvider(id , currentPage , state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    companyClientRelationDao.insertProviderKeys(response.content.map { article ->
                        ProviderRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.client?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.client?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map { company -> company.provider?.toCompany()})
                    companyClientRelationDao.insertClientProviderRelation(response.content.map { relation -> relation.toClientProviderRelation() })

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
    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return companyClientRelationDao.getFirstProviderRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return companyClientRelationDao.getLatestProviderRemoteKey()?.nextPage

    }

    private suspend fun deleteCache(){
        companyClientRelationDao.clearAllProviderTable(id)
        companyClientRelationDao.clearProviderRemoteKeysTable()
    }
}