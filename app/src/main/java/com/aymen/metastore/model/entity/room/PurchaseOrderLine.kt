package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.Status

@Entity(tableName = "purchase_order_line",
    foreignKeys = [
        ForeignKey(entity = PurchaseOrder::class, parentColumns = ["id"], childColumns = ["purchaseOrderId"]),
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"]),
        ForeignKey(entity = ArticleCompany::class, parentColumns = ["id"], childColumns = ["articleId"])
    ])
data class PurchaseOrderLine(

    @PrimaryKey(autoGenerate = true) val id: Long? = null,

    var quantity: Double? = 0.0,

    val comment: String? = null,

    val status: Status? = Status.INWAITING,

    val delivery: Boolean? = false,


    val createdDate : String? = null,

    val lastModifiedDate : String? = null,

    val purchaseOrderId: Long? = null,

    val invoiceId : Long? = null,

    var articleId: Long? = null

)
