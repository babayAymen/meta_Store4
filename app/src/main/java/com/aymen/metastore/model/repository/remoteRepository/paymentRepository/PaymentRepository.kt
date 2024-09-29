package com.aymen.store.model.repository.remoteRepository.paymentRepository

import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.entity.realm.Payment
import retrofit2.Response

interface PaymentRepository {

    suspend fun getAllMyPayments(): Response<List<Payment>>

    suspend fun getAllMyPaymentsEspeceByDate(date : String,findate : String):Response<List<PaymentForProviders>>

}