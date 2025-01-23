package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.AllInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesDeliveredRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.PaymentStatus

@OptIn(ExperimentalPagingApi::class)
class InvoiceDeliveredRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase
): RemoteMediator<Int , PurchaseOrderWithCompanyAndUserOrClient>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val purchaOrderDao = room.purchaseOrderDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PurchaseOrderWithCompanyAndUserOrClient>
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
            val response = api.getInvoicesDeliveredByMe(currentPage, state.config.pageSize)

            Log.e("tetsinvoice","remote mediator invoice delivery  : $response")
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

//            val isDataIncomplete = invoiceDao.getInvoiceCountBySource(source = true) == 0
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    purchaOrderDao.insertInvoicesDeliveredKeys(response.content.map { article ->
                        InvoicesDeliveredRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.client?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.client?.toCompany()})
                    companyDao.insertCompany(response.content.map {company -> company.company?.toCompany()})
                    purchaOrderDao.insertOrder(response.content.map {order -> order.toPurchaseOrder() })

                } catch (ex: Exception) {
                    Log.e("errorinvoice", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("errorinvoice", ex.message.toString())
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return purchaOrderDao.getFirstInvoicesDeliveredRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return purchaOrderDao.getLatestInvoicesDeliveredRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        purchaOrderDao.clearOrdersDelivered(true)
        purchaOrderDao.clearInvoicesDeliveredRemoteKeysTable()

    }
}