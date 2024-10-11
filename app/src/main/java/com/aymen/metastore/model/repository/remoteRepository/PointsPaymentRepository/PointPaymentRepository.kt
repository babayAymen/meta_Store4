package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import com.aymen.metastore.model.entity.api.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.api.PointsPaymentDto
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.PointsPayment
import retrofit2.Response

interface PointPaymentRepository {

    suspend fun sendPoints(pointsPayment: PointsPaymentDto):Response<Void>

    suspend fun getAllMyPointsPayment(companyId : Long) : Response<List<PointsPayment>>

    suspend fun getAllMyPaymentsEspece(companyId : Long) : Response<List<PaymentForProviders>>

    suspend fun getMyProfitByDate(beginDate : String, finalDate : String): Response<String>

    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDay>>

    suspend fun getMyHistoryProfitByDate(beginDate : String, finalDate : String): Response<List<PaymentForProviderPerDay>>



}