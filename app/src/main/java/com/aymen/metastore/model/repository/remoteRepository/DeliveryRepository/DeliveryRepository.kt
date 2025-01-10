package com.aymen.metastore.model.repository.remoteRepository.DeliveryRepository

import com.aymen.store.model.Enum.AccountType
import retrofit2.Response

interface DeliveryRepository {

    suspend fun addAsDelivery(userId : Long) : Response<AccountType>
}