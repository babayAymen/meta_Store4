package com.aymen.store.model.entity.dto

import com.aymen.store.model.Enum.DeliveryCategory

data class Delivery(

    val id : Long? = null,

    var user : UserDto,

    var rate : Long,

    var category : DeliveryCategory
)
