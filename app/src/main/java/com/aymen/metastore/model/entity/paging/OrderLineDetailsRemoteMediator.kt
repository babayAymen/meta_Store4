package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.OrderLineKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class OrderLineDetailsRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val orderId : Long
): RemoteMediator<Int, PurchaseOrderLineWithPurchaseOrderOrInvoice>() {


    private val articleCompanyDao = room.articleCompanyDao()
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()
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
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }
                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getOrdersLineDetails(orderId ,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    purchaseOrderLineDao.insertOrderLineKeys(response.map { article ->
                        OrderLineKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })
                    userDao.insertUser(response.map {user -> user.article?.company?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.article?.company?.toCompany()})
                    userDao.insertUser(response.map {user -> user.article?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map { company -> company.article?.provider?.toCompany() })
                    categoryDao.insertCategory(response.map {category -> category.article?.category?.toCategory()!! })
                    subCategoryDao.insertSubCategory(response.map {subCategory -> subCategory.article?.subCategory?.toSubCategory()!! })
                    articleDao.insertArticle(response.map {article -> article.article?.article?.toArticle()!! })
                    articleCompanyDao.insertArticle(response.map { it.article?.toArticleCompany(true)!! })
                    userDao.insertUser(response.map { user -> user.purchaseorder?.person?.toUser() })
                    userDao.insertUser(response.map { user -> user.purchaseorder?.company?.user?.toUser() })
                    userDao.insertUser(response.map { user -> user.purchaseorder?.client?.user?.toUser() })
                    companyDao.insertCompany(response.map { company -> company.purchaseorder?.company?.toCompany() })
                    companyDao.insertCompany(response.map { company -> company.purchaseorder?.client?.toCompany() })
                    purchaseOrderDao.insertOrder(response.map { order -> order.purchaseorder?.toPurchaseOrder()!! })
                    room.userDao().insertUser(response.map { invoice -> invoice.invoice?.person?.toUser()})
                    room.userDao().insertUser(response.map { invoice -> invoice.invoice?.client?.user?.toUser()})
                    room.companyDao().insertCompany(response.map { invoice -> invoice.invoice?.client?.toCompany()})
                    room.invoiceDao().insertInvoice(response.map { invoice -> invoice.invoice?.toInvoice()?:com.aymen.metastore.model.entity.room.entity.Invoice()})
                    purchaseOrderLineDao.insertOrderLine(response.map {line -> line.toPurchaseOrderLine()})

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

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PurchaseOrderLineWithPurchaseOrderOrInvoice>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { purchaseOrderLineDao.getAllOrderLineRemoteKeys(it.purchaseOrderLine.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, PurchaseOrderLineWithPurchaseOrderOrInvoice>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { purchaseOrderLineDao.getAllOrderLineRemoteKeys(it.purchaseOrderLine.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PurchaseOrderLineWithPurchaseOrderOrInvoice>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.purchaseOrderLine?.id?.let { purchaseOrderLineDao.getAllOrderLineRemoteKeys(it).nextPage }
    }

    private suspend fun deleteCache(){
        purchaseOrderLineDao.clearOrderLineKeys()
        purchaseOrderLineDao.clearOrderLine(orderId)
    }
}