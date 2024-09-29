package com.aymen.store.model.repository.remoteRepository.shoppingRepository

import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import retrofit2.Response

interface ShoppingRepository {

    suspend fun test(order : PurchaseOrderLineDto):Response<Void>

    suspend fun orderLineResponse(status : String, id : Long, isAll: Boolean) : Response<Void>

}