package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.DeliveryCategory


data class Delivery (


    val id : Long? = null,
    val user : User,
    val rate : Long,
    val category : DeliveryCategory? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = ""
    )