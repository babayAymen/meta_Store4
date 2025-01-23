package com.aymen.metastore.model.entity.model

data class PaymentForProviders (
    val id : Long ? = null,
    val giveenespece : Double? = 0.0,
    val status : Boolean? = false,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
    val purchaseOrder : PurchaseOrder? = null,
)