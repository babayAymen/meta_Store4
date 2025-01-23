package com.aymen.metastore.model.repository.remoteRepository.orderRepository
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.entity.dto.PurchaseOrderDto
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface OrderRepository {


    fun getAllMyOrdersNotAccepted(id  :Long) : Flow<PagingData<PurchaseOrder>>
    fun getPurchaqseOrderDetails(orderId : Long) : Flow<PagingData<PurchaseOrderLineWithPurchaseOrderOrInvoice>>

    suspend fun getAllMyOrdersLines(companyId : Long) : Response<List<PurchaseOrderDto>>

    suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) : Response<List<PurchaseOrderLineDto>>

    suspend fun sendOrder(orderList : List<PurchaseOrderLine>):Response<List<PurchaseOrderLineDto>>

    suspend fun getAllMyOrders(companyId : Long):Response<List<PurchaseOrderLineDto>>

     fun getAllOrdersLineByInvoiceId(companyId : Long ,invoiceId : Long): Flow<PagingData<PurchaseOrderLine>>

    fun getAllOrdersNotDelivered(id : Long) : Flow<PagingData<PurchaseOrder>>
}