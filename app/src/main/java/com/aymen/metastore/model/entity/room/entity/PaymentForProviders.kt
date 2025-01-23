package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PaymentForProviders

@Entity(tableName = "payment_for_providers",
    foreignKeys = [
        ForeignKey(entity = PurchaseOrder::class, parentColumns = ["purchaseOrderId"], childColumns = ["purchaseOrderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index("purchaseOrderId")]
)
data class PaymentForProviders(
    @PrimaryKey
    val id : Long ? = null,
    val purchaseOrderId : Long? = null,
    val giveenespece : Double? = 0.0,
    val status : Boolean? = false,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
) {
    fun toPaymentForProviders(purchaseOrder: com.aymen.metastore.model.entity.model.PurchaseOrder): PaymentForProviders{
        return PaymentForProviders(
            id = id,
            purchaseOrder = purchaseOrder,
            giveenespece = giveenespece,
            status = status,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
