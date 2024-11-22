package com.aymen.store.model.repository.remoteRepository.orderRepository

import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.store.model.repository.globalRepository.ServiceApi
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api : ServiceApi
) : OrderRepository {
    override suspend fun getAllMyOrdersLines(companyId : Long) = api.getAllMyOrder(companyId = companyId)
    override suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) = api.getAllMyOrdersLineByOrderId(orderId = orderId)
    override suspend fun sendOrder(orderList: List<PurchaseOrderLine>) = api.sendOrder(orderList)
    override suspend fun getAllMyOrders(companyId : Long) = api.getAllMyOrdersLine(companyId)
    override suspend fun getAllOrdersLineByInvoiceId(invoiceId: Long) = api.getAllOrdersLineByInvoiceId(invoiceId = invoiceId)


}