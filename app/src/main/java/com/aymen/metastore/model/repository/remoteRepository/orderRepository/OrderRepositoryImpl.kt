package com.aymen.metastore.model.repository.remoteRepository.orderRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.paging.remotemediator.OrderLineDetailsRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.OrderNotAcceptedRemoteMediator
import com.aymen.metastore.model.entity.paging.pagingsource.PurchaseOrderLinesByInvoiceIdPagingSource
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
) : OrderRepository {

    private val purchaseOrderDao = room.purchaseOrderDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyOrdersNotAccepted(id:Long): Flow<PagingData<PurchaseOrderWithCompanyAndUserOrClient>> {
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 2),
            remoteMediator = OrderNotAcceptedRemoteMediator(
                api = api, room = room,id = id
            ),
            pagingSourceFactory = {
                purchaseOrderLineDao.getAllMyOrdersNotAccepted(Status.INWAITING)
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPurchaqseOrderDetails(orderId: Long): Flow<PagingData<PurchaseOrderLineWithPurchaseOrderOrInvoice>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 2),
            remoteMediator = OrderLineDetailsRemoteMediator(
                api = api, room = room,orderId = orderId
            ),
            pagingSourceFactory = {
                purchaseOrderLineDao.getAllMyOrdersLinesByOrderId(orderId)
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    override suspend fun getAllMyOrdersLines(companyId : Long) = api.getAllMyOrder(companyId = companyId)
    override suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) = api.getAllMyOrdersLineByOrderId(orderId = orderId)
    override suspend fun sendOrder(orderList: List<PurchaseOrderLine>) = api.sendOrder(orderList)
    override suspend fun getAllMyOrders(companyId : Long) = api.getAllMyOrdersLine(companyId)

    override fun getAllOrdersLineByInvoiceId(companyId : Long ,invoiceId: Long) : Flow<PagingData<PurchaseOrderLine>>{
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Number of items per page
                enablePlaceholders = false // Disable placeholders for unloaded pages
            ),
            pagingSourceFactory = {
                PurchaseOrderLinesByInvoiceIdPagingSource(api, companyId, invoiceId)
            }
        ).flow
    }

    suspend fun insert(response : List<PurchaseOrderLineDto>){

        room.userDao().insertUser(response.map {user -> user.article?.company?.user?.toUser()})
        room.companyDao().insertCompany(response.map {company -> company.article?.company?.toCompany()})
        room.userDao().insertUser(response.map {user -> user.article?.provider?.user?.toUser()})
        room.companyDao().insertCompany(response.map { company -> company.article?.provider?.toCompany() })
        room.categoryDao().insertCategory(response.map {category -> category.article?.category?.toCategory() })
        room.subCategoryDao().insertSubCategory(response.map {subCategory -> subCategory.article?.subCategory?.toSubCategory() })
        room.articleDao().insertArticle(response.map {article -> article.article?.article?.toArticle(isMy = true) })
        room.articleCompanyDao().insertArticle(response.map { it.article?.toArticleCompany(true)})
        room.userDao().insertUser(response.map { user -> user.purchaseorder?.person?.toUser() })
        room.userDao().insertUser(response.map { user -> user.purchaseorder?.company?.user?.toUser() })
        room.userDao().insertUser(response.map { user -> user.purchaseorder?.client?.user?.toUser() })
        room.companyDao().insertCompany(response.map { company -> company.purchaseorder?.company?.toCompany() })
        room.companyDao().insertCompany(response.map { company -> company.purchaseorder?.client?.toCompany() })
        purchaseOrderDao.insertOrder(response.map { order -> order.purchaseorder?.toPurchaseOrder() })
        room.userDao().insertUser(response.map { invoice -> invoice.invoice?.person?.toUser()})
        room.userDao().insertUser(response.map { invoice -> invoice.invoice?.client?.user?.toUser()})
        room.companyDao().insertCompany(response.map { invoice -> invoice.invoice?.client?.toCompany()})
        room.invoiceDao().insertInvoice(response.map { invoice -> invoice.invoice?.toInvoice(isInvoice = false)})
        room.purchaseOrderLineDao().insertOrderLine(response.map { line -> line.toPurchaseOrderLine() })
    }

}