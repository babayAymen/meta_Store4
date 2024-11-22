package com.aymen.store.model.repository.remoteRepository.paymentRepository

import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi
) : PaymentRepository {
    override suspend fun getAllMyPaymentsEspeceByDate(
        date: String,
        findate: String
    ): Response<List<PaymentForProvidersDto>> {
        TODO("Not yet implemented")
    }


}