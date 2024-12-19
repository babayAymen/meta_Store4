package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.room.entity.PurchaseOrder

data class PurchaseOrder (

    val id : Long? = null,
    val company : Company? = null,
    val client : Company? = null,
    val person : User? = null,
    val createdDate : String? = null,
    val orderNumber : Long? = 0
){
    fun toPurchaseOrderEntity() : PurchaseOrder{
        return PurchaseOrder(
            purchaseOrderId = id,
            companyId = company?.id,
            userId = person?.id,
            clientId = client?.id,
            createdDate = createdDate,
            orderNumber = orderNumber

        )
    }
}