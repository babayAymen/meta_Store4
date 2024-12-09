package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.remoteKeys.InCompleteRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderProfitHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.globalRepository.ServiceApi

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
    private val subCategoryDao = room.subCategoryDao()
    private val categoryDao = room.categoryDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val invoiceDao = room.invoiceDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()

//    override suspend fun initialize(): InitializeAction {
//        val remoteKeyCount = paymentForProvidersDao.getRemoteKeyCount()
//        return if (remoteKeyCount > 0) {
//            // Remote keys exist; resume from stored state
//            InitializeAction.SKIP_INITIAL_REFRESH
//        } else {
//            // No keys exist; force a fresh load
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        }
//    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PaymentForProvidersWithCommandLine>
    ): MediatorResult {

        return try {

            Log.e("PagingState", "Load type: ${loadType} state : $state")
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getPreviousPageForTheFirstItem(state)
                    remoteKeys?.nextPage?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    val remoteKey = getPreviousPageForTheFirstItem(state)
                    val prevPage = remoteKey?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null
                    )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKey = getNextPageForTheLastItem(state)
                    val nextPage = remoteKey?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null
                    )
                    nextPage
                }
            }
            val response = api.getAllProvidersProfit(id,currentPage!!, state.config.pageSize)
            Log.e("remoteKey", "remote key apped type : ${response}")
            val endOfPaginationReached = response.last
            val prevPage = if(response.number== 0) null else response.number -1
            val nextPage = if(response.last) null else response.number +1
            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentForProvidersDao.insertProviderProfitHistoryKeys(response.content.map { article ->
                        ProviderProfitHistoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

//                    response.lastOrNull()?.let { lastItem ->
//                        paymentForProvidersDao.insertProviderProfitHistoryKeys(
//                            ProviderProfitHistoryRemoteKeysEntity(
//                                id = lastItem.id!!,
//                                nextPage = nextPage,
//                                prevPage = null
//                            )
//                        )
//                    }
//                    response.firstOrNull()?.let { firstItem ->
//                        paymentForProvidersDao.insertProviderProfitHistoryKeys(
//                            ProviderProfitHistoryRemoteKeysEntity(
//                                id = firstItem.id!!,
//                                nextPage = null,
//                                prevPage = prevPage
//                            )
//                        )
//                    }
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
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.article?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.article?.provider?.toCompany()})
                    userDao.insertUser(response.content.map {user -> user.purchaseOrderLine?.article?.category?.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.purchaseOrderLine?.article?.category?.company?.toCompany()})
                    purchaseOrderDao.insertOrder(response.content.map { order -> order.purchaseOrderLine?.purchaseorder?.toPurchaseOrder()?: PurchaseOrder() })
                    categoryDao.insertCategory(response.content.map { cat -> cat.purchaseOrderLine?.article?.category?.toCategory()?: Category() })
                    subCategoryDao.insertSubCategory(response.content.map { cat -> cat.purchaseOrderLine?.article?.subCategory?.toSubCategory()?: SubCategory() })
                    articleDao.insertArticle(response.content.map { article -> article.purchaseOrderLine?.article?.article?.toArticle(isMy = true)?: Article() })
                    articleCompanyDao.insertArticle(response.content.map { article -> article.purchaseOrderLine?.article?.toArticleCompany(false)?: ArticleCompany() })
                    invoiceDao.insertInvoice(response.content.map { invoice -> invoice.purchaseOrderLine?.invoice?.toInvoice()?:Invoice() })
                    purchaseOrderLineDao.insertOrderLine(response.content.map { line -> line.purchaseOrderLine?.toPurchaseOrderLine()?: PurchaseOrderLine() })
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

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): ProviderProfitHistoryRemoteKeysEntity? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let {
            paymentForProvidersDao.getProvidersProfitHistoryRemoteKey(it.paymentForProviders.id?:0)
        }
        return remoteKey
    }

    private suspend fun getNextPageForTheLastItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): ProviderProfitHistoryRemoteKeysEntity? {
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
        return lastItem?.let {
            paymentForProvidersDao.getProvidersProfitHistoryRemoteKey(it.paymentForProviders.id?:0)
        }
    }


    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PaymentForProvidersWithCommandLine>): ProviderProfitHistoryRemoteKeysEntity? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey =  entity?.paymentForProviders?.id?.let {
            paymentForProvidersDao.getProvidersProfitHistoryRemoteKey(it) }
        return remoteKey

    }

    private suspend fun deleteCache(){
        paymentForProvidersDao.clearAllProvidersProfitHistoryTable()
        paymentForProvidersDao.clearAllProvidersProfitHistoryRemoteKeysTable()
    }
}