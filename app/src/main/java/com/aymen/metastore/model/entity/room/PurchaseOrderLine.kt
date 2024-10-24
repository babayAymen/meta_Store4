package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.Status

@Entity(tableName = "purchase_order_line",
    foreignKeys = [
        ForeignKey(entity = PurchaseOrder::class, parentColumns = ["id"], childColumns = ["purchaseOrderId"]),
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"])
    ])
data class PurchaseOrderLine(

    @PrimaryKey(autoGenerate = true) val id: Long? = null,

    val quantity: Double? = 0.0,

    val comment: String? = null,

    val status: Status? = Status.INWAITING,

    val delivery: Boolean? = false,

    val purchaseOrderId: Long? = null,

    val createdDate : String? = null,

    val lastModifiedDate : String? = null,

    val invoiceId : Long? = null
)
