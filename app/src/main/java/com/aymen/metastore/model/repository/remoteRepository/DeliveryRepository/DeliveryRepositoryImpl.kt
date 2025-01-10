package com.aymen.metastore.model.repository.remoteRepository.DeliveryRepository

import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.AccountType
import retrofit2.Response
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val api : ServiceApi
): DeliveryRepository {
    override suspend fun addAsDelivery(userId: Long): Response<AccountType> = api.addAsDelivery(userId)

}