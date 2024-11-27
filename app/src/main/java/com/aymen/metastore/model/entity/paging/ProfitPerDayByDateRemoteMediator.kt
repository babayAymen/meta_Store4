package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayByDateRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ProfitPerDayByDateRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
    private val beginDate : String,
    private val finalDate : String
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
            val response = api.getMyHistoryProfitByDate(id,beginDate, finalDate,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentForProviderPerDayDao.insertPointsPaymentByDateKeys(response.map { article ->
                        PointsPaymentPerDayByDateRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.provider?.toCompany()})
                    paymentForProviderPerDayDao.insertPaymentForProviderPerDay(response.map { payment -> payment.toPaymentForProviderPerDay() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PaymentPerDayWithProvider>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { paymentForProviderPerDayDao.getPaymentForProviderPerDayByDateRemoteKey(it.paymentForProviderPerDay.id!!) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, PaymentPerDayWithProvider>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey = entity?.let { paymentForProviderPerDayDao.getPaymentForProviderPerDayByDateRemoteKey(it.paymentForProviderPerDay.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PaymentPerDayWithProvider>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey = entity?.paymentForProviderPerDay?.id?.let { paymentForProviderPerDayDao.getPaymentForProviderPerDayByDateRemoteKey(it) }
        return remoteKey?.nextPage
    }

    private suspend fun deleteCache(){
        paymentForProviderPerDayDao.clearAllpaymentForProviderPerDayTable()
        paymentForProviderPerDayDao.clearAllpaymentForProviderPerDayByDateRemoteKeysTable()
    }
}