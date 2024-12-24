package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine
import com.aymen.store.model.Enum.Status

data class PurchaseOrderLine (

    val id : Long? = null,
    val purchaseorder: PurchaseOrder? = null,
    val article: ArticleCompany? = null,
    val quantity: Double? = 0.0,
    val comment: String? = null,
    val status: Status? = Status.INWAITING,
    val delivery: Boolean? = false,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val invoice : Invoice? = null
){
    fun toPurchaseOrderLineEntity() : PurchaseOrderLine{
        return PurchaseOrderLine(
            purchaseOrderLineId = id ,
            purchaseOrderId = purchaseorder?.id,
            articleId = article?.id,
            quantity = quantity,
            comment = comment,
            status = status,
            delivery = delivery,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            invoiceId = invoice?.id
        )
    }
}