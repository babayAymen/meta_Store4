package com.aymen.store.model.entity.api

import com.aymen.store.model.Enum.DeliveryStatus

data class OrderDelivery(

    val id : Long? = null,

    var delivery: Delivery,

    var purchaseOrder: PurchaseOrderDto,

    var status : DeliveryStatus,

    var note : String,

    var deliveryConfirmed : Boolean
)
