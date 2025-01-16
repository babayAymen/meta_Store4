package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.OrderLineKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class OrderLineDetailsRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val orderId : Long
): RemoteMediator<Int, PurchaseOrderLineWithPurchaseOrderOrInvoice>() {


    private val articleCompanyDao = room.articleCompanyDao()
//    private val categoryDao = room.categoryDao()
//    private val subCategoryDao = room.subCategoryDao()
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val articleDao = room.articleDao()
    private val purchaseOrderDao = room.purchaseOrderDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PurchaseOrderLineWithPurchaseOrderOrInvoice>
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
            val response = api.getOrdersLineDetails(orderId ,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    purchaseOrderLineDao.insertOrderLineKeys(response.content.map { article ->
                        OrderLineKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })
                    userDao.insertUser(response.content.map {user -> user.article?.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.article?.company?.toCompany()})
                    articleDao.insertArticle(response.content.map {article -> article.article?.article?.toArticle(isMy = true) })
                    articleCompanyDao.insertArticle(response.content.map { it.article?.toArticleCompany(true)})
                    userDao.insertUser(response.content.map { user -> user.purchaseorder?.person?.toUser() })
                    userDao.insertUser(response.content.map { user -> user.purchaseorder?.company?.user?.toUser() })
                    userDao.insertUser(response.content.map { user -> user.purchaseorder?.client?.user?.toUser() })
                    companyDao.insertCompany(response.content.map { company -> company.purchaseorder?.company?.toCompany() })
                    companyDao.insertCompany(response.content.map { company -> company.purchaseorder?.client?.toCompany() })
                    purchaseOrderDao.insertOrder(response.content.map { order -> order.purchaseorder?.toPurchaseOrder() })
                    room.userDao().insertUser(response.content.map { invoice -> invoice.invoice?.person?.toUser()})
                    room.userDao().insertUser(response.content.map { invoice -> invoice.invoice?.client?.user?.toUser()})
                    room.companyDao().insertCompany(response.content.map { invoice -> invoice.invoice?.client?.toCompany()})
                    room.invoiceDao().insertInvoice(response.content.map { invoice -> invoice.invoice?.toInvoice(isInvoice = false)})
                    purchaseOrderLineDao.insertOrderLine(response.content.map {line -> line.toPurchaseOrderLine()})

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
        return purchaseOrderLineDao.getFirstOrderLineKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return purchaseOrderLineDao.getLatestOrderLineKey()?.nextPage
      }

    private suspend fun deleteCache(){
        purchaseOrderLineDao.clearOrderLineKeys()
        purchaseOrderLineDao.clearOrderLine(orderId)
    }
}