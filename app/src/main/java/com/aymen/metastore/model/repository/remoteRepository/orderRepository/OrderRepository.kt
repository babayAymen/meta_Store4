package com.aymen.store.model.repository.remoteRepository.orderRepository
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.entity.dto.PurchaseOrderDto
import retrofit2.Response

interface OrderRepository {

    suspend fun getAllMyOrdersLines(companyId : Long) : Response<List<PurchaseOrderDto>>

    suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) : Response<List<PurchaseOrderLineDto>>

    suspend fun sendOrder(orderList : List<PurchaseOrderLineDto>):Response<Void>

    suspend fun getAllMyOrders(companyId : Long):Response<List<PurchaseOrderLineDto>>

    suspend fun getAllOrdersLineByInvoiceId(invoiceId : Long): Response<List<PurchaseOrderLineDto>>

}