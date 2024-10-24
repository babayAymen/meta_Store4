package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import com.aymen.metastore.model.entity.Dto.PointsPaymentDto
import com.aymen.store.model.repository.globalRepository.ServiceApi
import javax.inject.Inject

class PointPaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi
)
    : PointPaymentRepository {
    override suspend fun sendPoints(pointsPayment: PointsPaymentDto) = api.sendPoints(pointsPayment)
    override suspend fun getAllMyPointsPayment(companyId: Long) = api.getAllMyPointsPayment(companyId)
    override suspend fun getAllMyPointsPaymentt(companyId: Long) = api.getAllMyPointsPaymentt(companyId)
    override suspend fun getAllMyPaymentsEspece(companyId: Long) = api.getAllMyPaymentsEspece(companyId)
    override suspend fun getAllMyPaymentsEspecee(companyId: Long) = api.getAllMyPaymentsEspecee(companyId)
    override suspend fun getMyProfitByDate(beginDate: String, finalDate: String) = api.getMyProfitByDate(beginDate, finalDate)
    override suspend fun getAllMyProfits() = api.getAllMyProfits()
    override suspend fun getAllMyProfitss() = api.getAllMyProfitss()
    override suspend fun getMyHistoryProfitByDate(beginDate: String, finalDate: String) = api.getMyHistoryProfitByDate(beginDate, finalDate)
    override suspend fun getMyHistoryProfitByDatee(beginDate: String, finalDate: String) = api.getMyHistoryProfitByDatee(beginDate, finalDate)


}