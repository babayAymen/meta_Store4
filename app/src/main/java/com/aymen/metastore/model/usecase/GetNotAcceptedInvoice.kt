package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetNotAcceptedInvoice (private val repository: PaymentRepository) {
    operator fun invoke(id : Long) : Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return repository.getNotAcceptedInvoice(id)
    }
}