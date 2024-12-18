package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import kotlinx.coroutines.flow.Flow

class GetAllInvoicesAsClientAndStatus(private val repository: InvoiceRepository) {

    operator fun invoke(clientId : Long, status : Status) : Flow<PagingData<Invoice>>{
            return repository.getAllInvoicesAsClientAndStatus(clientId, status)
    }
}