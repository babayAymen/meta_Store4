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
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.PaymentStatus

@OptIn(ExperimentalPagingApi::class)
class AllInvoiceRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
    private val status : PaymentStatus
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
            val response = api.getAllMyInvoicesAsProvider(id,status,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            val isDataIncomplete = invoiceDao.getInvoiceCountBySource(source = true) == 0
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH && isDataIncomplete){
                        deleteCache()
                    }
                    invoiceDao.insertAllInvoiceKeys(response.content.map { article ->
                        AllInvoiceRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.person?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.client?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.client?.toCompany()})
                    companyDao.insertCompany(response.content.map {company -> company.provider?.toCompany()})
                    invoiceDao.insertInvoice(response.content.map {invoice -> invoice.toInvoice(isInvoice = true) })

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
        return invoiceDao.getFirstAllInvoiceRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return invoiceDao.getLatestAllInvoiceRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        when(status){
            PaymentStatus.ALL ->  invoiceDao.clearAllTableAsProvider(id)
            else ->  invoiceDao.clearAllTableAsProviderAndStatus(id, status)
        }
            invoiceDao.clearAllRemoteKeysTable()

    }
}