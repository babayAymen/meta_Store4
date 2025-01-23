package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.BuyHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class BuyHistoryMediator( // a verifier maybe i dont use it
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
): RemoteMediator<Int, InvoiceWithClientPersonProvider>() {

    private val invoiceDao = room.invoiceDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val purchaseOrderDao = room.purchaseOrderDao()

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
            val response = api.getAllBuyHistory(id,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    invoiceDao.insertBuyHistoryKeys(response.content.map { article ->
                        BuyHistoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.client?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.client?.toCompany()})
                    companyDao.insertCompany(response.content.map {company -> company.provider?.toCompany()})
                    purchaseOrderDao.insertOrder(response.content.map{ order -> order.purchaseOrder?.toPurchaseOrder()})
                    invoiceDao.insertInvoice(response.content.map {category -> category.toInvoice(isInvoice = true) })

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
        return invoiceDao.getFirstBuyHistoryRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return invoiceDao.getLatestBuyHistoryRemoteKeys()?.nextPage
    }

    private suspend fun deleteCache(){
//        invoiceDao.clearAllTableAsProvider(id, false)
        invoiceDao.clearAllBuyHistoryRemoteKeysTable()
    }

}