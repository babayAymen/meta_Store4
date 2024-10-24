package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import com.aymen.metastore.model.entity.Dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.Dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.Dto.PointsPaymentDto
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.entity.realm.PointsPayment
import retrofit2.Response

interface PointPaymentRepository {

    suspend fun sendPoints(pointsPayment: PointsPaymentDto):Response<Void>

    suspend fun getAllMyPointsPayment(companyId : Long) : Response<List<PointsPaymentDto>>
    suspend fun getAllMyPointsPaymentt(companyId : Long) : Response<List<PointsPayment>>

    suspend fun getAllMyPaymentsEspece(companyId : Long) : Response<List<PaymentForProvidersDto>>
    suspend fun getAllMyPaymentsEspecee(companyId : Long) : Response<List<PaymentForProviders>>

    suspend fun getMyProfitByDate(beginDate : String, finalDate : String): Response<String>

    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDayDto>>
    suspend fun getAllMyProfitss(): Response<List<PaymentForProviderPerDay>>

    suspend fun getMyHistoryProfitByDate(beginDate : String, finalDate : String): Response<List<PaymentForProviderPerDayDto>>
    suspend fun getMyHistoryProfitByDatee(beginDate : String, finalDate : String): Response<List<PaymentForProviderPerDay>>



}