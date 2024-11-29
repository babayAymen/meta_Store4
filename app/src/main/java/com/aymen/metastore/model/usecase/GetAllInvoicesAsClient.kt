package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.store.model.Enum.AccountType
import kotlinx.coroutines.flow.Flow

class GetAllInvoicesAsClient(private val repository : InvoiceRepository) {

    operator fun invoke(userId : Long, accountType : AccountType) : Flow<PagingData<InvoiceWithClientPersonProvider>>{
        return repository.getAllInvoicesAsClient(clientId = userId, accountType = accountType)
    }
}