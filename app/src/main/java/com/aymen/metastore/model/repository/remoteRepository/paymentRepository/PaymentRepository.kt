package com.aymen.store.model.repository.remoteRepository.paymentRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dao.PaymentDao
import com.aymen.metastore.model.entity.dto.CashDto
import com.aymen.metastore.model.entity.dto.PaymentDto
import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PaymentRepository {
    suspend fun getAllMyPaymentsEspeceByDate(date : String,findate : String):Response<List<PaymentForProvidersDto>>
    fun getAllMyBuyHistory(id : Long) : Flow<PagingData<InvoiceWithClientPersonProvider>>
    fun getNotAcceptedInvoice(id : Long, isProvider : Boolean, status: Status) : Flow<PagingData<Invoice>>
    suspend fun sendRaglement(companyId : Long , cashDto : CashDto) : Response<PaymentDto>
    fun getPaymentHystoricByInvoiceId(invoiceId : Long) : Flow<PagingData<Payment>>
}