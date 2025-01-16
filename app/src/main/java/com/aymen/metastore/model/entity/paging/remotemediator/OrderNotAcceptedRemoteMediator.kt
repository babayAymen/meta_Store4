package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class OrderNotAcceptedRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
): RemoteMediator<Int , PurchaseOrderWithCompanyAndUserOrClient>() {


    private val articleCompanyDao = room.articleCompanyDao()
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val articleDao = room.articleDao()
    private val purchaseOrderDao = room.purchaseOrderDao()
    private val invoiceDao = room.invoiceDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()


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
            val response = api.getAllMyOrdersNotAccepted(id ,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    purchaseOrderDao.insertOrderNotAcceptedKeys(response.content.map { article ->
                        OrderNotAcceptedKeysEntity(
                            id = article.purchaseorder?.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })
                    userDao.insertUser(response.content.map {user -> user.article?.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.article?.company?.toCompany()})
                    articleDao.insertArticle(response.content.map {article -> article.article?.article?.toArticle(isMy = true) })
                    articleCompanyDao.insertArticle(response.content.map { it.article?.toArticleCompany(true) })
                    userDao.insertUser(response.content.map { user -> user.purchaseorder?.person?.toUser() })
                    userDao.insertUser(response.content.map { user -> user.purchaseorder?.company?.user?.toUser() })
                    userDao.insertUser(response.content.map { user -> user.purchaseorder?.client?.user?.toUser() })
                    companyDao.insertCompany(response.content.map { company -> company.purchaseorder?.company?.toCompany() })
                    companyDao.insertCompany(response.content.map { company -> company.purchaseorder?.client?.toCompany() })
                    purchaseOrderDao.insertOrder(response.content.map { order -> order.purchaseorder?.toPurchaseOrder() })
                    userDao.insertUser(response.content.map { invoice -> invoice.invoice?.person?.toUser()})
                    userDao.insertUser(response.content.map { invoice -> invoice.invoice?.client?.user?.toUser()})
                    companyDao.insertCompany(response.content.map { invoice -> invoice.invoice?.client?.toCompany()})
                    invoiceDao.insertInvoice(response.content.map { invoice -> invoice.invoice?.toInvoice(isInvoice = false)})
                    purchaseOrderLineDao.insertOrderLine(response.content.map { line -> line.toPurchaseOrderLine() })
                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex.message}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("error", "articlecompany ${ex.message}")
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
      return purchaseOrderDao.getFirstAllPurchaseOrderNotAcceptedKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
      return purchaseOrderDao.getLatestAllOrderNotAcceptedKey()?.nextPage
    }

    private suspend fun deleteCache(){
        purchaseOrderDao.clearAllOrderNotAcceptedKeys()
        purchaseOrderDao.clearAllPurchaseOrderNotAccepted()
    }
}