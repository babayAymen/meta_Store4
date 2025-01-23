package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.repository.remoteRepository.DeliveryRepository.DeliveryRepository
import kotlinx.coroutines.flow.Flow

class GetInvoicesIdelevered(private val repository : DeliveryRepository) {
    operator fun invoke() : Flow<PagingData<PurchaseOrder>>{
        return repository.getInvoicesIdelevered()
    }
}