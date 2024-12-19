package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PointPaymentRepository {

    fun getAllMyPaymentsEspeceByDate(id : Long, beginDate : String, finalDate : String) : Flow<PagingData<PaymentForProvidersWithCommandLine>>
//    fun getAllMyPaymentNotAccepted() : Flow<PagingData<PointsWithProviderclientcompanyanduser>>
//    fun getAllMyPaymentFromInvoice(companyId : Long ,status : PaymentStatus) : Flow<PagingData<PointsWithProviderclientcompanyanduser>>
    fun getAllRechargeHistory(id : Long) : Flow<PagingData<PointsPayment>>
    fun getAllMyProfitsPerDay(companyId: Long) : Flow<PagingData<PaymentForProviderPerDay>>
    fun getAllMyPointsPaymentForPoviders(companyId : Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>>
    suspend fun sendPoints(pointsPayment: PointsPaymentDto):Response<Void>


    fun getAllMyPaymentsEspece(companyId : Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>>

    suspend fun getMyProfitByDate(beginDate : String, finalDate : String): Response<String>

    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDayDto>>

     fun getMyHistoryProfitByDate(id : Long, beginDate : String, finalDate : String): Flow<PagingData<PaymentPerDayWithProvider>>


}