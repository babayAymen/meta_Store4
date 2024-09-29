package com.aymen.store.model.repository.remoteRepository.shoppingRepository

import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private  val api : ServiceApi
) : ShoppingRepository {
    override suspend fun test(order: PurchaseOrderLineDto) = api.test(order)
    override suspend fun orderLineResponse(status: String, id: Long, isAll: Boolean) = api.orderLineResponse(id , status, isAll)

}