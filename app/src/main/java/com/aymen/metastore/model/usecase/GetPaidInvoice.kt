package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaidInvoice(private val repository: PaymentRepository) {
    operator fun invoke(id : Long, isProvider : Boolean, paymentStatus: PaymentStatus) : Flow<PagingData<Invoice>>{
        return repository.getPaidInvoice(id, isProvider, paymentStatus)
    }
}