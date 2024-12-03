package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentForProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class PointsEspeceRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
):RemoteMediator<Int, PaymentForProvidersWithCommandLine>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()
    private val invoiceDao = room.invoiceDao()
    private val paymentForProvidersDao = room.paymentForProvidersDao()
    private val purchaseOrderDao = room.purchaseOrderDao()
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PaymentForProvidersWithCommandLine>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
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
            val response = api.getAllProvidersProfit(id,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentForProvidersDao.insertKeys(response.map { article ->
                        PointsPaymentForProviderRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.purchaseorder?.company?.user?.toUser()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.purchaseorder?.person?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.purchaseorder?.company?.toCompany()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.purchaseorder?.client?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.purchaseorder?.client?.toCompany()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.invoice?.person?.toUser()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.invoice?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.invoice?.provider?.toCompany()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.invoice?.client?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.invoice?.client?.toCompany()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.article?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.article?.provider?.toCompany()})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.article?.category?.company?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.article?.category?.company?.toCompany()})
                    purchaseOrderDao.insertOrder(response.map { order -> order.purchaseOrderLine?.purchaseorder?.toPurchaseOrder()!! })
                    categoryDao.insertCategory(response.map { cat -> cat.purchaseOrderLine?.article?.category?.toCategory()!! })
                    subCategoryDao.insertSubCategory(response.map { cat -> cat.purchaseOrderLine?.article?.subCategory?.toSubCategory()!! })
                    articleDao.insertArticle(response.map { article -> article.purchaseOrderLine?.article?.article?.toArticle(isMy = true)!! })
                    articleCompanyDao.insertArticle(response.map { article -> article.purchaseOrderLine?.article?.toArticleCompany(false)!! })
                    invoiceDao.insertInvoice(response.map { invoice -> invoice.purchaseOrderLine?.invoice?.toInvoice()!! })
                    purchaseOrderLineDao.insertOrderLine(response.map { line -> line.purchaseOrderLine?.toPurchaseOrderLine()!! })
                    paymentForProvidersDao.insertPaymentForProviders(response.map {payment -> payment.toPaymentForProviders() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { paymentForProvidersDao.getRemoteKeys(it.paymentForProviders.id!!) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey = entity?.let { paymentForProvidersDao.getRemoteKeys(it.paymentForProviders.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey = entity?.paymentForProviders?.id?.let { paymentForProvidersDao.getRemoteKeys(it) }
        return remoteKey?.nextPage
    }

    private suspend fun deleteCache(){
        paymentForProvidersDao.clearRemoteKeys()
        paymentForProvidersDao.clearPointsPayment()
    }
}