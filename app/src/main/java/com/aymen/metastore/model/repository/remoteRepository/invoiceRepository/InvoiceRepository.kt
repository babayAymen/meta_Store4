package com.aymen.metastore.model.repository.remoteRepository.invoiceRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.store.model.Enum.PaymentStatus
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InvoiceRepository {

     fun getAllMyInvoicesAsProvider(companyId : Long, isProvider : Boolean, status : PaymentStatus) : Flow<PagingData<Invoice>>
    fun getAllInvoicesAsClient(clientId : Long, accountType : AccountType, status: PaymentStatus) : Flow<PagingData<Invoice>>
    fun getAllInvoicesAsClientAndStatus(clientId: Long , status : Status) : Flow<PagingData<Invoice>>
    fun getAllCommandLineByInvoiceId(companyId : Long , invoiceId : Long) : Flow<PagingData<CommandLine>>


//    suspend fun getAllMyInvoicesAsClient(companyId : Long) : Response<List<InvoiceDto>>
    suspend fun getLastInvoiceCode(asProvider : Boolean):Response<Long>
    suspend fun addInvoice(commandLineDtos : List<CommandLine>,
                           clientId : Long,
                           invoiceCode : Long,
                           discount : Double,
                           clientType :  AccountType,
                           invoiceMode: InvoiceMode,
                           asProvider : Boolean
    ):Response<List<CommandLineDto>>

    suspend fun getAllMyInvoicesAsClientAndStatus(id : Long , status : Status):Response<List<InvoiceDto>>

    suspend fun accepteInvoice(invoiceId : Long, status : Status): Response<Void>
    suspend fun getAllMyPaymentNotAccepted(companyId : Long) : Response<List<InvoiceDto>>
    fun searchInvoice(type : SearchPaymentEnum, text : String, companyId : Long) : Flow<PagingData<Invoice>>
    suspend fun deleteInvoiceById(invoiceId : Long) : Response<Void>
    suspend fun acceptInvoiceAsDelivery(orderId : Long) : Response<Boolean>
    suspend fun submitOrderDelivered(orderId : Long, code : String) : Response<Boolean>
    suspend fun userRejectOrder(orderId : Long) : Response<Void>
}