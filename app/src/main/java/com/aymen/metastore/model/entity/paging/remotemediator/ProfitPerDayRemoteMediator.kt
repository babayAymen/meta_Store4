package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ProfitPerDayRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long?
)
    : RemoteMediator<Int, PaymentPerDayWithProvider>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val paymentForProviderPerDayDao = room.paymentForProviderPerDayDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PaymentPerDayWithProvider>
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
            val response = api.getAllProfitPerDay(id!!,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else response.number - 1
            val nextPage = if (response.last) null else response.number + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentForProviderPerDayDao.insertPointsPaymentKeys(response.content.map { article ->
                       PointsPaymentPerDayRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.receiver?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.receiver?.toCompany()})
                    paymentForProviderPerDayDao.insertPaymentForProviderPerDay(response.content.map { payment -> payment.toPaymentForProviderPerDay() })

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
        return paymentForProviderPerDayDao.getFirstPaymentForProviderPerDayRemoteKeys()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return paymentForProviderPerDayDao.getLatestPaymentForProviderPerDayRemoteKeys()?.nextPage

    }

    private suspend fun deleteCache(){
        paymentForProviderPerDayDao.clearAllpaymentForProviderPerDayTable()
        paymentForProviderPerDayDao.clearAllpaymentForProviderPerDayRemoteKeysTable()
    }
}