package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderProfitHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ProfitOfProviderMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
): RemoteMediator<Int, PaymentForProvidersWithCommandLine>() {

    private val paymentForProvidersDao = room.paymentForProvidersDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val purchaseOrderDao = room.purchaseOrderDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val invoiceDao = room.invoiceDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()


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
                    val previousePage = getPreviousPageForTheFirstItem(state)
                    val prevPage = previousePage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    prevPage
                }
                LoadType.APPEND -> {
                    val nextePage = getNextPageForTheLastItem(state)
                    val nextPage = nextePage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    nextPage
                }
            }
            val response = api.getAllProvidersProfit(id,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if(response.number== 0) null else response.number -1
            val nextPage = if(response.last) null else response.number +1

            val empty = paymentForProvidersDao.existRecords() == 0
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH && empty){
                        deleteCache()
                    }
                    paymentForProvidersDao.insertProviderProfitHistoryKeys(response.content.map { article ->
                        ProviderProfitHistoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })


                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.purchaseorder?.company?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.purchaseorder?.person?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.purchaseorder?.company?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.purchaseorder?.client?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.purchaseorder?.client?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.invoice?.person?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.invoice?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.invoice?.provider?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.invoice?.client?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.invoice?.client?.toCompany()})
                    purchaseOrderDao.insertOrder(response.content.map { order -> order.purchaseOrderLine?.purchaseorder?.toPurchaseOrder() })
//                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.article?.provider?.user?.toUser()})
//                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.article?.provider?.toCompany()})
//                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.article?.category?.company?.user?.toUser()})
//                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.article?.category?.company?.toCompany()})
//                    categoryDao.insertCategory(response.content.map { cat -> cat.purchaseOrderLine?.article?.category?.toCategory(isCategory = false) })
//                    subCategoryDao.insertSubCategory(response.content.map { cat -> cat.purchaseOrderLine?.article?.subCategory?.toSubCategory() })
                    articleDao.insertArticle(response.content.map { article -> article.purchaseOrderLine?.article?.article?.toArticle(isMy = true)})
                    articleCompanyDao.insertArticle(response.content.map { article -> article.purchaseOrderLine?.article?.toArticleCompany(article.purchaseOrderLine.article?.isDeleted?:true) })
                    invoiceDao.insertInvoiceelse(response.content.map { invoice -> invoice.purchaseOrderLine?.invoice?.toInvoice(isInvoice = false) })
                    purchaseOrderLineDao.insertOrderLine(response.content.map { line -> line.purchaseOrderLine?.toPurchaseOrderLine() })
                    paymentForProvidersDao.insertPaymentForProviders(response.content.map {payment -> payment.toPaymentForProviders() })

                } catch (ex: Exception) {
                    Log.e("errorprofitmediator", ex.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("errorprofitmediator", ex.toString())
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { paymentForProvidersDao.getProvidersProfitHistoryRemoteKey(it.paymentForProviders.id?:0) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLastItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }
            val entity = lastItem?.data?.lastOrNull()
        val remoteKey = entity?.let {
            paymentForProvidersDao.getProvidersProfitHistoryRemoteKey(it.paymentForProviders.id!!)
        }
        return remoteKey?.nextPage
    }


    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey =  entity?.paymentForProviders?.id?.let { paymentForProvidersDao.getProvidersProfitHistoryRemoteKey(it) }
        return remoteKey?.nextPage

    }

    private suspend fun deleteCache(){
        paymentForProvidersDao.clearAllProvidersProfitHistoryTable()
        paymentForProvidersDao.clearAllProvidersProfitHistoryRemoteKeysTable()


    }
}