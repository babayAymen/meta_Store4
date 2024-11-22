package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.OrderDelivery
import com.aymen.store.model.Enum.DeliveryStatus
@Entity(tableName = "order_delivery",
    foreignKeys = [
        ForeignKey(entity = Delivery::class, parentColumns = ["id"], childColumns = ["deliveryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = PurchaseOrder::class,
            parentColumns = ["id"],
            childColumns = ["purchaseOrderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ])
data class OrderDelivery(

    @PrimaryKey
    val id : Long? = null,
    val deliveryId : Long? = null ,
    val purchaseOrderId : Long? = null,
    val status : DeliveryStatus? = DeliveryStatus.PENDING,
    val note : String,
    val deliveryConfirmed : Boolean
){
    fun toOrderDelivery(delivery: com.aymen.metastore.model.entity.model.Delivery,
                        purchaseOrder: com.aymen.metastore.model.entity.model.PurchaseOrder) : OrderDelivery{
        return OrderDelivery(
            id = id,
            delivery = delivery,
            purchaseOrder = purchaseOrder,
            status = status,
            note = note,
            deliveryConfirmed = deliveryConfirmed
        )
    }
}
