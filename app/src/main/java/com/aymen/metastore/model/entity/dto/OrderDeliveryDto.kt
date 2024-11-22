package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.OrderDelivery
import com.aymen.store.model.Enum.DeliveryStatus

data class OrderDeliveryDto(

    val id : Long? = null,
    var delivery: DeliveryDto,
    var purchaseOrder: PurchaseOrderDto,
    var status : DeliveryStatus,
    var note : String,
    var deliveryConfirmed : Boolean
){
    fun toOrderDelivery() : OrderDelivery {

        return OrderDelivery(
            id = id,
            deliveryId = delivery.id,
            purchaseOrderId = purchaseOrder.id,
            status = status,
            note = note,
            deliveryConfirmed = deliveryConfirmed
        )
    }
}
