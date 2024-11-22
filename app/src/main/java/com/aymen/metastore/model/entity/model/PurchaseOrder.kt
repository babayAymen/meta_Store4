package com.aymen.metastore.model.entity.model

data class PurchaseOrder (

    val id : Long? = null,
    val company : Company? = null,
    val client : Company? = null,
    val person : User? = null,
    val createdDate : String? = null,
    val orderNumber : Long? = 0
)