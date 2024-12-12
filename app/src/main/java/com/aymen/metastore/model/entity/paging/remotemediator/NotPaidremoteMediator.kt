package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.NotPayedRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class NotPaidremoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
    private val isProvider : Boolean
): RemoteMediator<Int, InvoiceWithClientPersonProvider>() {

    private val invoiceDao = room.invoiceDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InvoiceWithClientPersonProvider>
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
            val response =
                if(isProvider)api.getAllBuyHistoryByPaidStatusAsProvider(id, PaymentStatus.NOT_PAID,currentPage, state.config.pageSize)
                else
                api.getAllBuyHistoryByPaidStatusAsClient(id, PaymentStatus.NOT_PAID,currentPage, state.config.pageSize)

            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    invoiceDao.insertBuyHistoryNotPaidKeys(response.map { article ->
                        NotPayedRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

                    userDao.insertUser(response.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.map {user -> user.client?.user?.toUser()})
                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.client?.toCompany()})
                    companyDao.insertCompany(response.map {company -> company.provider?.toCompany()})
//                    invoiceDao.insertInvoice(response.map {category -> category.toInvoice() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { invoiceDao.getBuyHistoryNotPaidRemoteKey(it.invoice.id!!) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey = entity?.let { invoiceDao.getBuyHistoryNotPaidRemoteKey(it.invoice.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey = entity?.invoice?.id?.let { invoiceDao.getBuyHistoryNotPaidRemoteKey(it) }
        return remoteKey?.nextPage
    }

    private suspend fun deleteCache(){
     //   invoiceDao.clearAllBuyHistoryTableByPaidStatus(PaymentStatus.NOT_PAID)
//        invoiceDao.clearAllTableAsProvider(id, false)
        invoiceDao.clearAllRemoteKeysTable()
        invoiceDao.clearAllBuyHistoryNotPaidRemoteKeysTable()
    }
}