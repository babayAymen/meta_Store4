package com.aymen.metastore.model.repository.remoteRepository.invoiceRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InvoiceRepository {

     fun getAllMyInvoicesAsProvider(companyId : Long) : Flow<PagingData<InvoiceWithClientPersonProvider>>
     fun getAllInvoicesAsClient(clientId : Long, accountType : AccountType) : Flow<PagingData<InvoiceWithClientPersonProvider>>
     fun getAllInvoicesAsClientAndStatus(clientId: Long , status : Status) : Flow<PagingData<InvoiceWithClientPersonProvider>>



//    suspend fun getAllMyInvoicesAsClient(companyId : Long) : Response<List<InvoiceDto>>
    suspend fun getLastInvoiceCode():Response<Long>
    suspend fun addInvoice(commandLineDtos : List<CommandLine>,
                           clientId : Long,
                           invoiceCode : Long,
                           discount : Double,
                           clientType :  AccountType,
                           invoiceMode: InvoiceMode):Response<Void>

    suspend fun getAllMyInvoicesAsClientAndStatus(id : Long , status : Status):Response<List<InvoiceDto>>

    suspend fun accepteInvoice(invoiceId : Long, status : Status): Response<Void>

//    suspend fun getAllMyInvoicesAsProviderAndStatus(companyId : Long, status : PaymentStatus) : Response<List<InvoiceDto>>

    suspend fun getAllMyPaymentNotAccepted(companyId : Long) : Response<List<InvoiceDto>>


}