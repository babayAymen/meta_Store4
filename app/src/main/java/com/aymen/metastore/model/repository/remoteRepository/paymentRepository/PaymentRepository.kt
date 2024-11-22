package com.aymen.store.model.repository.remoteRepository.paymentRepository

import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import retrofit2.Response

interface PaymentRepository {
    suspend fun getAllMyPaymentsEspeceByDate(date : String,findate : String):Response<List<PaymentForProvidersDto>>

}