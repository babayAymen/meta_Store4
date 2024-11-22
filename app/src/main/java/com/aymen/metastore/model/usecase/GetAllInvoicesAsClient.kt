package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import kotlinx.coroutines.flow.Flow

class GetAllInvoicesAsClient(private val repository : InvoiceRepository) {

    operator fun invoke(userId : Long) : Flow<PagingData<InvoiceWithClientPersonProvider>>{
        return repository.getAllInvoicesAsClient(clientId = userId)
    }
}