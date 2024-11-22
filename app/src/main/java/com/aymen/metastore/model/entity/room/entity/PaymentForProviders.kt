package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PaymentForProviders

@Entity(tableName = "payment_for_providers",
    foreignKeys = [
        ForeignKey(entity = PurchaseOrderLine::class, parentColumns = ["id"], childColumns = ["purchaseOrderLineId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index("purchaseOrderLineId")]
)
data class PaymentForProviders(
    @PrimaryKey
    val id : Long ? = null,
    val purchaseOrderLineId : Long? = null,
    val giveenespece : Double? = 0.0,
    val status : Boolean? = false,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
) {
    fun toPaymentForProviders(purchaseOrderLine: com.aymen.metastore.model.entity.model.PurchaseOrderLine): PaymentForProviders{
        return PaymentForProviders(
            id = id,
            purchaseOrderLine = purchaseOrderLine,
            giveenespece = giveenespece,
            status = status,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
