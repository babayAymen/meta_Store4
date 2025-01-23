package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentForProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class PointsEspeceByDateRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long,
    private val beginDate : String,
    private val finalDate : String,

    ):RemoteMediator<Int, PaymentForProvidersWithCommandLine>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()
    private val invoiceDao = room.invoiceDao()
    private val paymentForProvidersDao = room.paymentForProvidersDao()
    private val purchaseOrderDao = room.purchaseOrderDao()

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
            val response =
                api.getAllMyPaymentsEspeceByDate(id,beginDate, finalDate,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
//                        deleteCache()
                    }
                    paymentForProvidersDao.insertKeys(response.content.map { article ->
                        PointsPaymentForProviderRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })
                    userDao.insertUser(response.content.map {user -> user.purchaseOrder?.company?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrder?.person?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrder?.company?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrder?.client?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrder?.client?.toCompany()})
//                    userDao.insertUser(response.content.map {user -> user.purchaseOrder?.invoice?.person?.toUser()})
//                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.invoice?.provider?.user?.toUser()})
//                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.invoice?.provider?.toCompany()})
//                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.invoice?.client?.user?.toUser()})
//                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.invoice?.client?.toCompany()})
                    purchaseOrderDao.insertOrder(response.content.map { order -> order.purchaseOrder?.toPurchaseOrder() })
//                    articleDao.insertArticle(response.content.map { article -> article.purchaseOrderLine?.article?.article?.toArticle(isMy = true) })
//                    articleCompanyDao.insertArticle(response.content.map { article -> article.purchaseOrderLine?.article?.toArticleCompany(false) })
//                    invoiceDao.insertInvoice(response.content.map { invoice -> invoice.purchaseOrderLine?.invoice?.toInvoice(isInvoice = false) })
//                    purchaseOrderLineDao.insertOrderLine(response.content.map { line -> line.purchaseOrderLine?.toPurchaseOrderLine() })
                    paymentForProvidersDao.insertPaymentForProviders(response.content.map {payment -> payment.toPaymentForProviders() })

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
        return paymentForProvidersDao.getFirstRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return paymentForProvidersDao.getLatestRemoteKey()?.nextPage
    }

//    private suspend fun deleteCache(){
//        paymentForProvidersDao.clearRemoteKeys()
//        paymentForProvidersDao.clearPointsPayment()
//    }
}