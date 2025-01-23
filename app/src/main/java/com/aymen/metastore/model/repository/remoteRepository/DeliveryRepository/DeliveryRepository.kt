package com.aymen.metastore.model.repository.remoteRepository.DeliveryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.store.model.Enum.AccountType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface DeliveryRepository {

    suspend fun addAsDelivery(userId : Long) : Response<AccountType>

    fun getInvoicesIdelevered() : Flow<PagingData<PurchaseOrder>>
}