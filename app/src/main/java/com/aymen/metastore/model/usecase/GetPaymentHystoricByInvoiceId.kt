package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentHystoricByInvoiceId(private val repository : PaymentRepository) {
    operator fun invoke(invoiceId : Long) : Flow<PagingData<Payment>>{
        return repository.getPaymentHystoricByInvoiceId(invoiceId)
    }
}