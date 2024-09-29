package com.aymen.store.model.entity.realm

import com.aymen.store.model.Enum.DeliveryStatus
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey


class OrderDelivery : RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var delivery: Delivery? = null

    var order: PurchaseOrderLine? = null

    var status: String? = DeliveryStatus.PENDING.toString()

    var note: String? = null

    var deliveryCofrimed: Boolean? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}