package com.aymen.store.model.repository.remoteRepository.shoppingRepository

import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import retrofit2.Response

interface ShoppingRepository {

    suspend fun test(order : PurchaseOrderLineDto):Response<Void>

    suspend fun orderLineResponse(status : Status, ids : List<Long>) : Response<Double>

}