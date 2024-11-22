package com.aymen.store.model.repository.remoteRepository.shoppingRepository

import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private  val api : ServiceApi
) : ShoppingRepository {
    override suspend fun test(order: PurchaseOrderLineDto) = api.test(order)
    override suspend fun orderLineResponse(status: Status, id: Long, isAll: Boolean): Response<Double> = api.orderLineResponse(id , status, isAll)

}