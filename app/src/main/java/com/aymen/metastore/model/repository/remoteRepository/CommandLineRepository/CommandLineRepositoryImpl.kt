package com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository

import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class CommandLineRepositoryImpl @Inject constructor(
    private val api : ServiceApi
) : CommandLineRepository {
    override suspend fun getAllCommandLinesByInvoiceId(invoiceId: Long): Response<List<CommandLineDto>> {
        TODO("Not yet implemented")
    }

}