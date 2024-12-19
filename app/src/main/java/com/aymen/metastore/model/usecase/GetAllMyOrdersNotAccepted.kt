package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyOrdersNotAccepted(private val repository : OrderRepository) {

    operator fun invoke(id : Long):Flow<PagingData<PurchaseOrder>>{
        return repository.getAllMyOrdersNotAccepted(id)
    }
}