package com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository

import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.realm.CommandLine
import retrofit2.Response

interface CommandLineRepository {

    suspend fun getAllCommandLinesByInvoiceId(invoiceId : Long):Response<List<CommandLine>>

}