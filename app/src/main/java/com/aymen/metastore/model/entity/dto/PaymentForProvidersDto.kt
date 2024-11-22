package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PaymentForProviders

data class PaymentForProvidersDto(
    val id : Long ? = null,
    val purchaseOrderLine : PurchaseOrderLineDto? = null,
    val giveenespece : Double? = 0.0,
    val status : Boolean? = false,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
){
    fun toPaymentForProviders() : PaymentForProviders {

        return PaymentForProviders(
            id = id,
            purchaseOrderLineId = purchaseOrderLine?.id,
            giveenespece = giveenespece,
            status = status,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
