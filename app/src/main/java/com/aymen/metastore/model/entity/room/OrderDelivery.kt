package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.DeliveryStatus
@Entity(tableName = "order_delivery",
    foreignKeys = [
        ForeignKey(entity = Delivery::class, parentColumns = ["id"], childColumns = ["deliveryId"]),
        ForeignKey(
            entity = PurchaseOrder::class,
            parentColumns = ["id"],
            childColumns = ["purchaseOrderId"],
            onDelete = ForeignKey.SET_NULL
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
)
