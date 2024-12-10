package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetNotAcceptedInvoice (private val repository: PaymentRepository) {
    operator fun invoke(id : Long, isProvider : Boolean, status : Status) : Flow<PagingData<Invoice>> {
        return repository.getNotAcceptedInvoice(id, isProvider, status)
    }
}