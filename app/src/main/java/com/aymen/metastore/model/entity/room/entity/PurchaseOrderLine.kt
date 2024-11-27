package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.store.model.Enum.Status

@Entity(tableName = "purchase_order_line",
    foreignKeys = [
        ForeignKey(entity = PurchaseOrder::class, parentColumns = ["purchaseOrderId"], childColumns = ["purchaseOrderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = ArticleCompany::class, parentColumns = ["id"], childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index("purchaseOrderId"), Index("invoiceId"), Index("articleId")])
data class PurchaseOrderLine(

    @PrimaryKey(autoGenerate = false) val purchaseOrderLineId: Long? = null,
    var quantity: Double? = 0.0,
    val comment: String? = null,
    val status: Status? = Status.INWAITING,
    val delivery: Boolean? = false,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val purchaseOrderId: Long? = null,
    val invoiceId : Long? = null,
    var articleId: Long? = null
){
    fun toPurchaseOrderLine(purchaseOrder: com.aymen.metastore.model.entity.model.PurchaseOrder?,
                            invoice: com.aymen.metastore.model.entity.model.Invoice?,
                            article: com.aymen.metastore.model.entity.model.ArticleCompany?): PurchaseOrderLine{
        return PurchaseOrderLine(
            id = purchaseOrderLineId,
            quantity = quantity,
            comment = comment,
            status = status,
            delivery = delivery,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            purchaseorder = purchaseOrder,
            invoice = invoice,
            article = article,
        )
    }
}
