package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.store.model.Enum.PaymentStatus
import kotlinx.coroutines.flow.Flow

class GetAllInvoices(private val repository: InvoiceRepository) {

    operator fun invoke(companyId : Long, isProvider : Boolean, status : PaymentStatus): Flow<PagingData<Invoice>>{
        return repository.getAllMyInvoicesAsProvider(companyId = companyId, isProvider, status)
    }

}