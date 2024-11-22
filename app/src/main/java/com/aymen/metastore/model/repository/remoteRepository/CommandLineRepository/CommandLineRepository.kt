package com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository

import com.aymen.metastore.model.entity.dto.CommandLineDto
import retrofit2.Response

interface CommandLineRepository {

    suspend fun getAllCommandLinesByInvoiceId(invoiceId : Long):Response<List<CommandLineDto>>

}