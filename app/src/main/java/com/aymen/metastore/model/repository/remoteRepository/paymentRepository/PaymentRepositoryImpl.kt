package com.aymen.store.model.repository.remoteRepository.paymentRepository

import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.repository.globalRepository.ServiceApi
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi
) : PaymentRepository {
    override suspend fun getAllMyPaymentsEspeceByDate(date: String,findate : String) = api.getAllMyPaymentsEspeceByDate(date,findate)
 }