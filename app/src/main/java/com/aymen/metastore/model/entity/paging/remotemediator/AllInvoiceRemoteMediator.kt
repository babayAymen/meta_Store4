package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.AllInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class AllInvoiceRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
): RemoteMediator<Int, InvoiceWithClientPersonProvider>()  {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val invoiceDao = room.invoiceDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InvoiceWithClientPersonProvider>
    ): MediatorResult {
        return try {
        Log.e("loadTypeallinvoice","load type : $loadType")
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val r = getNextPageClosestToCurrentPosition(state)
                    Log.e("loadTypeallinvoice","refrechtype and r : $r")
                    r?.minus(1) ?: 0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    Log.e("loadTypeallinvoice","prepand type and prev page : $previousPage")
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    Log.e("loadTypeallinvoice","append type and next page : $nextPage")
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    nextePage
                }
            }
            val response = api.getAllMyInvoicesAsProvider(id,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            val isDataIncomplete = invoiceDao.getInvoiceCountBySource(source = true) == 0
            Log.e("loadTypeallinvoice","there is  : $isDataIncomplete")
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH && isDataIncomplete){
                        deleteCache()
                    }
                    invoiceDao.insertAllInvoiceKeys(response.map { article ->
                        AllInvoiceRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.map {user -> user.client?.user?.toUser()})
                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.client?.toCompany()})
                    companyDao.insertCompany(response.map {company -> company.provider?.toCompany()})
                    invoiceDao.insertInvoice(response.map {invoice -> invoice.toInvoice(isInvoice = true) })

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

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { invoiceDao.getAllInvoiceRemoteKey(it.invoice.id!!) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey = entity?.let { invoiceDao.getAllInvoiceRemoteKey(it.invoice.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, InvoiceWithClientPersonProvider>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey = entity?.invoice?.id?.let { invoiceDao.getAllInvoiceRemoteKey(it) }
        return remoteKey?.nextPage
    }


    private suspend fun deleteCache(){
        try {
            Log.d("deleteCache", "Clearing invoices and remote keys for provider: $id")
            invoiceDao.clearAllTableAsProvider(id)

            Log.d("deleteCache", "Cleared all invoices")
            invoiceDao.clearAllRemoteKeysTable()
            Log.d("deleteCache", "Cleared all invoices and remote keys")
        } catch (ex: Exception) {
            Log.e("deleteCache", "Error while clearing cache: ${ex.message}", ex)
        }
    }
}