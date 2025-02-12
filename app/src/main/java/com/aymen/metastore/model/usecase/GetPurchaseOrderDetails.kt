package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetPurchaseOrderDetails(private val repository : OrderRepository) {

    operator fun invoke(orderId : Long): Flow<PagingData<PurchaseOrderLine>>{
        return repository.getPurchaqseOrderDetails(orderId = orderId)
    }
}