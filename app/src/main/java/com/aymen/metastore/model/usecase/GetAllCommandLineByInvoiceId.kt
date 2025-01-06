package com.aymen.metastore.model.usecase

import android.util.Log
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import kotlinx.coroutines.flow.Flow

class GetAllCommandLineByInvoiceId(private val repository : InvoiceRepository) {

    operator fun invoke(companyId : Long , invoiceId : Long) : Flow<PagingData<CommandLine>>{
        return repository.getAllCommandLineByInvoiceId(companyId , invoiceId)
    }
}