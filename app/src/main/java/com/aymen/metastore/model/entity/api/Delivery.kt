package com.aymen.store.model.entity.api

import com.aymen.store.model.Enum.DeliveryCategory

data class Delivery(

    val id : Long? = null,

    var user : UserDto,

    var rate : Long,

    var category : DeliveryCategory
)
