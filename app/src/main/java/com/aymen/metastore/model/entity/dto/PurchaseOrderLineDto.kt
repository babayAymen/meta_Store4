package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine
import com.aymen.store.model.Enum.Status

data class PurchaseOrderLineDto(

    val id : Long? = null,
    var article: ArticleCompanyDto? = null,
    var quantity: Double? = 0.0,
    var comment: String? = null,
    val status: Status? = Status.INWAITING,
    var delivery: Boolean? = false,
    val purchaseorder: PurchaseOrderDto? = null,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val invoice : InvoiceDto? = null
){
    fun toPurchaseOrderLine() : PurchaseOrderLine {

        return PurchaseOrderLine(
            purchaseOrderLineId = id,
            articleId = article?.id,
            quantity = quantity,
            comment = comment,
            status = status,
            delivery = delivery,
            purchaseOrderId = purchaseorder?.id,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            invoiceId = invoice?.id
        )
    }
}
