package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import kotlinx.coroutines.flow.Flow

class SearchInvoice( private val repository : InvoiceRepository) {
    operator fun invoke(type : SearchPaymentEnum, text : String, companyId : Long) : Flow<PagingData<Invoice>>{
        return repository.searchInvoice(type , text, companyId)
    }
}