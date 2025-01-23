package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetAllOrdersNotAcceptedAsDelivery(private val repository : OrderRepository) {

    operator fun invoke(id : Long) : Flow<PagingData<PurchaseOrder>>{
        return repository.getAllOrdersNotDelivered(id)
    }
}