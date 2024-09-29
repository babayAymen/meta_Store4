package com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository

import com.aymen.store.model.entity.realm.CommandLine
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class CommandLineRepositoryImpl @Inject constructor(
    private val api : ServiceApi
) : CommandLineRepository {
    override suspend fun getAllCommandLinesByInvoiceId(invoiceId: Long) = api.getAllCommandLinesByInvoiceId(invoiceId)
}