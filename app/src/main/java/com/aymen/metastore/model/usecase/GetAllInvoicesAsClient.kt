package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import kotlinx.coroutines.flow.Flow

class GetAllInvoicesAsClient(private val repository : InvoiceRepository) {

    operator fun invoke(userId : Long, accountType : AccountType, status : PaymentStatus) : Flow<PagingData<Invoice>>{
        return repository.getAllInvoicesAsClient(clientId = userId, accountType = accountType, status = status)
    }
}