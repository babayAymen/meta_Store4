package com.aymen.store.model.repository.remoteRepository.invoiceRepository

import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.dto.CommandLineDto
import com.aymen.store.model.entity.dto.InvoiceDto
import retrofit2.Response

interface InvoiceRepository {

    suspend fun getAllMyInvoicesAsProvider(companyId : Long) : Response<List<InvoiceDto>>
    suspend fun getAllMyInvoicesAsClient(companyId : Long) : Response<List<InvoiceDto>>
    suspend fun getLastInvoiceCode():Response<Long>
    suspend fun addInvoice(commandLineDtos : List<CommandLineDto>,
                           clientId : Long,
                           invoiceCode : Long,
                           discount : Double,
                           clientType :  AccountType,
                           invoiceMode: InvoiceMode):Response<Void>

    suspend fun getAllMyInvoicesAsClientAndStatus(id : Long , status : Status):Response<List<InvoiceDto>>

    suspend fun accepteInvoice(invoiceId : Long, status : Status): Response<Void>

    suspend fun getAllMyInvoicesAsProviderAndStatus(companyId : Long, status : PaymentStatus) : Response<List<InvoiceDto>>

    suspend fun getAllMyPaymentNotAccepted(companyId : Long) : Response<List<InvoiceDto>>


}