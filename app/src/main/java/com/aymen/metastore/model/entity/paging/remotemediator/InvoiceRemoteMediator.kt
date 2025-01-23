package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus

@OptIn(ExperimentalPagingApi::class)
class InvoiceRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val type : AccountType,
    private val id : Long,
    private val status : PaymentStatus
): RemoteMediator<Int, InvoiceWithClientPersonProvider>()  {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val invoiceDao = room.invoiceDao()
    private val purchaseOrderDao = room.purchaseOrderDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: androidx.paging.LoadType,
        state: PagingState<Int, InvoiceWithClientPersonProvider>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                androidx.paging.LoadType.REFRESH -> {
                    0
                }

                androidx.paging.LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem()
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                androidx.paging.LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem()
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getAllMyInvoicesAsClient(id,status,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == androidx.paging.LoadType.REFRESH){
                        deleteCache()
                    }
                    invoiceDao.insertKeys(response.content.map { article ->
                        InvoiceRemoteKeysEntity(
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
                    invoiceDao.insertInvoice(response.content.map {invoice -> invoice.toInvoice(isInvoice = true) })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return invoiceDao.getFirstInvoiceRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return invoiceDao.getLatestInvoiceRemoteKey()?.nextPage
    }
    private suspend fun deleteCache(){
        when(status){
            PaymentStatus.ALL -> if(type == AccountType.COMPANY) invoiceDao.clearAllTableAsClient(id) else invoiceDao.clearAllTableAsPerson(id)
            else -> if(type == AccountType.COMPANY) invoiceDao.clearAllInvoiceTableAsClientAndPaid(status, id) else invoiceDao.clearAllInvoiceTableAsPersonAndPaid(status, id)
        }
        invoiceDao.clearInvoiceRemoteKeysTable()
    }
}