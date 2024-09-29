package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import com.aymen.metastore.model.entity.api.PointsPaymentDto
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class PointPaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi
)
    : PointPaymentRepository {
    override suspend fun sendPoints(pointsPayment: PointsPaymentDto) = api.sendPoints(pointsPayment)
    override suspend fun getAllMyPointsPayment(companyId: Long) = api.getAllMyPointsPayment(companyId)
    override suspend fun getAllMyPaymentsEspece(companyId: Long) = api.getAllMyPaymentsEspece(companyId)
    override suspend fun getMyProfitByDate(beginDate: String, finalDate: String) = api.getMyProfitByDate(beginDate, finalDate)
    override suspend fun getAllMyProfits() = api.getAllMyProfits()
    override suspend fun getMyHistoryProfitByDate(beginDate: String, finalDate: String) = api.getMyHistoryProfitByDate(beginDate, finalDate)


}