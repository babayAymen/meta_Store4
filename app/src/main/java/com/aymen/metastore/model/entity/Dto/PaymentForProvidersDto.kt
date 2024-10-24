package com.aymen.metastore.model.entity.Dto

import com.aymen.store.model.entity.dto.PurchaseOrderLineDto

data class PaymentForProvidersDto(
    val id : Long ? = null,

    val purchaseOrderLine : PurchaseOrderLineDto? = null,

    val giveenespece : Double? = 0.0,

    val status : Boolean? = false,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
