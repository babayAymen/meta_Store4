package com.aymen.store.model.repository.remoteRepository.invoiceRepository

import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.api.CommandLineDto
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val api: ServiceApi
) : InvoiceRepository {
    override suspend fun getAllMyInvoicesAsProvider(companyId : Long) =  api.getAllMyInvoicesAsProvider(companyId = companyId)
    override suspend fun getAllMyInvoicesAsClient(companyId : Long) = api.getAllMyInvoicesAsClient(companyId = companyId)
    override suspend fun getLastInvoiceCode() = api.getLastInvoiceCode()
    override suspend fun addInvoice(commandLineDtos: List<CommandLineDto>,
                                    clientId : Long, invoiceCode : Long,
                                    discount : Double, clientTYpe : AccountType,
                                    invoiceMode: InvoiceMode
                                        ) = api.addInvoice(commandLineDtos,clientId,invoiceCode,discount, clientTYpe, invoiceMode)

    override suspend fun getAllMyInvoicesNotAccepted() = api.getAllMyInvoicesNotAccepted()
    override suspend fun accepteInvoice(invoiceId: Long, status: Status) = api.acceptInvoice(invoiceId , status)
    override suspend fun getAllMyInvoicesAsProviderAndStatus(companyId: Long, status: PaymentStatus) = api.getAllMyInvoicesAsProviderAndStatus(companyId, status)
    override suspend fun getAllMyPaymentNotAccepted(companyId: Long) = api.getAllMyPaymentNotAccepted(companyId)

}