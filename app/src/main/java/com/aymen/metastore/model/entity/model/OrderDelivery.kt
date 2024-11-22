package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.DeliveryStatus

data class OrderDelivery(

    val id : Long? = null,
    var delivery: Delivery,
    var purchaseOrder: PurchaseOrder,
    var status : DeliveryStatus? = DeliveryStatus.PENDING,
    var note : String,
    var deliveryConfirmed : Boolean
)