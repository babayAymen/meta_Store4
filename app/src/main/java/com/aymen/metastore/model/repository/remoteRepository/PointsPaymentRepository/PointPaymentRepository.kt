package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.store.model.Enum.PaymentStatus
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PointPaymentRepository {

    fun getAllMyPaymentsEspeceByDate(id : Long, beginDate : String, finalDate : String) : Flow<PagingData<PaymentForProvidersWithCommandLine>>
//    fun getAllMyPaymentNotAccepted() : Flow<PagingData<PointsWithProviderclientcompanyanduser>>
//    fun getAllMyPaymentFromInvoice(companyId : Long ,status : PaymentStatus) : Flow<PagingData<PointsWithProviderclientcompanyanduser>>
     fun getAllMyPointsPayment(companyId : Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>>

    suspend fun sendPoints(pointsPayment: PointsPaymentDto):Response<Void>


    fun getAllMyPaymentsEspece(companyId : Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>>

    suspend fun getMyProfitByDate(beginDate : String, finalDate : String): Response<String>

    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDayDto>>

    suspend fun getMyHistoryProfitByDate(beginDate : String, finalDate : String): Response<List<PaymentForProviderPerDayDto>>



}