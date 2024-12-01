package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesAsClientAndStatusRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class InvoiceAsClientAndStatusRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
    private val status : Status
): RemoteMediator<Int, InvoiceWithClientPersonProvider>()  {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val invoiceDao = room.invoiceDao()

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
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }

                androidx.paging.LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    previousePage
                }

                androidx.paging.LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    nextePage
                }
            }
            val response =
                api.getAllMyInvoicesAsClientAndStatus(id, status,currentPage, state.config.pageSize)
Log.e("invoiceresponse","response ${response.size} $response")
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == androidx.paging.LoadType.REFRESH){
                        deleteCache()
                    }
                    invoiceDao.insertInvoicesAsClientAndStatusKeys(response.map { article ->
                        InvoicesAsClientAndStatusRemoteKeysEntity(
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
                    invoiceDao.insertInvoice(response.map {invoice -> invoice.toInvoice() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val reomteKey = entity?.let { invoiceDao.getInvoiceAsClientAnStatusRemoteKey(it.invoice.id!!) }
        return reomteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val reomteKey = entity?.let { invoiceDao.getInvoiceAsClientAnStatusRemoteKey(it.invoice.id!!) }
        return reomteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val reomteKey = entity?.invoice?.id?.let { invoiceDao.getInvoiceAsClientAnStatusRemoteKey(it) }
        return reomteKey?.nextPage
    }

    private suspend fun deleteCache(){

        invoiceDao.clearAllInvoiceTableAsClientAnStatus(status,id)
        invoiceDao.clearInvoicesAsClientAndStatusRemoteKeysTable()
    }
}