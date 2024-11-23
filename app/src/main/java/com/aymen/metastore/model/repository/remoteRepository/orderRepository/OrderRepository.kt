package com.aymen.metastore.model.repository.remoteRepository.orderRepository
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.entity.dto.PurchaseOrderDto
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.metastore.util.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface OrderRepository {


    fun getAllMyOrdersNotAccepted(id  :Long) : Flow<PagingData<PurchaseOrderWithCompanyAndUserOrClient>>
    fun getPurchaqseOrderDetails(orderId : Long) : Flow<PagingData<PurchaseOrderLineWithPurchaseOrderOrInvoice>>

    suspend fun getAllMyOrdersLines(companyId : Long) : Response<List<PurchaseOrderDto>>

    suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) : Response<List<PurchaseOrderLineDto>>

    suspend fun sendOrder(orderList : List<PurchaseOrderLine>):Response<Void>

    suspend fun getAllMyOrders(companyId : Long):Response<List<PurchaseOrderLineDto>>

    suspend fun getAllOrdersLineByInvoiceId(invoiceId : Long): Response<List<PurchaseOrderLineDto>>

}