package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetInCompleteInvoice (private val repository: PaymentRepository) {
    operator fun invoke(id : Long, isProvider : Boolean) : Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return repository.getInCompleteInvoice(id, isProvider)
    }
}