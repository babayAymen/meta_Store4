package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ReglementForProviderRemoteKeys
import com.aymen.metastore.model.entity.room.remoteKeys.WorkerRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.ReglementWithPaymentPerDay
import com.aymen.metastore.model.entity.roomRelation.WorkerWithUser
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ReglementForProviderRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val paymentId : Long
) : RemoteMediator<Int , ReglementWithPaymentPerDay>(){

    private val paymentForProviderPerDayDao = room.paymentForProviderPerDayDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ReglementWithPaymentPerDay>
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
            val response = api.getAllReglementHistoryByPaymentId(paymentId,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentForProviderPerDayDao.insertReglementForProviderKeys(response.content.map { article ->
                        ReglementForProviderRemoteKeys(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                      userDao.insertUser(response.content.map {user -> user.meta?.toUser()})
                      userDao.insertUser(response.content.map {user -> user.payer?.user?.toUser()})
                      companyDao.insertCompany(response.content.map {user -> user.payer?.toCompany()})
                    paymentForProviderPerDayDao.insertPaymentForProvider(response.content.map { payment -> payment.paymentForProviderPerDay?.toPaymentForProviderPerDay()})
                    paymentForProviderPerDayDao.insertReglementForProvider(response.content.map { payment -> payment.toReglementEntity() })


                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("error", "articlecompany ${ex.message}")
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return paymentForProviderPerDayDao.getFirstReglementRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return paymentForProviderPerDayDao.getLatestReglementRemoteKey()?.nextPage
    }


    private suspend fun deleteCache(){
        paymentForProviderPerDayDao.clearAllReglementRemoteKeysTable()
        paymentForProviderPerDayDao.clearAllReglementTable()
    }
}