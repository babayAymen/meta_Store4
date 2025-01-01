package com.aymen.store.model.repository.remoteRepository.paymentRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PaymentRepository {
    suspend fun getAllMyPaymentsEspeceByDate(date : String,findate : String):Response<List<PaymentForProvidersDto>>
    fun getAllMyBuyHistory(id : Long) : Flow<PagingData<InvoiceWithClientPersonProvider>>
//    fun getPaidInvoice(id : Long, isProvider : Boolean, paymentStatus: PaymentStatus) : Flow<PagingData<Invoice>>
//    fun getNotPaidInvoice(id : Long, isProvider : Boolean) : Flow<PagingData<InvoiceWithClientPersonProvider>>
//    fun getInCompleteInvoice(id : Long, isProvider : Boolean) : Flow<PagingData<InvoiceWithClientPersonProvider>>
    fun getNotAcceptedInvoice(id : Long, isProvider : Boolean, status: Status) : Flow<PagingData<Invoice>>
}