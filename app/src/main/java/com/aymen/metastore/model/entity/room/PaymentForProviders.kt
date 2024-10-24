package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "payment_for_providers",
    foreignKeys = [
        ForeignKey(entity = PurchaseOrderLine::class, parentColumns = ["id"], childColumns = ["purchaseOrderLineId"])
    ]
)
data class PaymentForProviders(
    @PrimaryKey
    val id : Long ? = null,

    val purchaseOrderLineId : Long? = null,

    val giveenespece : Double? = 0.0,

    val status : Boolean? = false,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
