package com.aymen.store.model.repository.remoteRepository.orderRepository

import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.api.PurchaseOrderDto
import com.aymen.store.model.entity.realm.PurchaseOrder
import retrofit2.Response

interface OrderRepository {

    suspend fun getAllMyOrdersLines(companyId : Long) : Response<List<PurchaseOrder>>

    suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) : Response<List<PurchaseOrderLineDto>>

    suspend fun sendOrder(orderList : List<PurchaseOrderLineDto>):Response<Void>

    suspend fun getAllMyOrders(companyId : Long):Response<List<PurchaseOrderLineDto>>

    suspend fun getAllOrdersLineByInvoiceId(invoiceId : Long): Response<List<PurchaseOrderLineDto>>
}