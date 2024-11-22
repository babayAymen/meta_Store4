package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Delivery
import com.aymen.store.model.Enum.DeliveryCategory

data class DeliveryDto(

    val id : Long? = null,
    var user : UserDto,
    var rate : Long,
    var category : DeliveryCategory
){
    fun toDelivery() : Delivery {

        return Delivery(
            id = id,
            user = user.id,
            rate = rate,
            category = category,
        )
    }
}
