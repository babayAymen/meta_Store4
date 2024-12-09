package com.aymen.metastore.model.usecase

import android.util.Log
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepository
import com.aymen.metastore.util.Resource
import kotlinx.coroutines.flow.Flow

class GetAllOrdersLineByInvoiceId(private val repository : OrderRepository) {

    operator fun invoke(companyId : Long ,invoiceId : Long) : Flow<PagingData<PurchaseOrderLine>>{
        return repository.getAllOrdersLineByInvoiceId(companyId,invoiceId)
    }
}