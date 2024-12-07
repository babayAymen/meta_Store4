package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.model.ArticleCompany
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

    fun toPurchaseOrderLineModel() : com.aymen.metastore.model.entity.model.PurchaseOrderLine{
        return com.aymen.metastore.model.entity.model.PurchaseOrderLine(

            id = id,
            article = article?.toArticleCompanyModel(),
            quantity = quantity,
            comment = comment,
            status = status,
            delivery = delivery,
            purchaseorder = purchaseorder?.toPurchaseOrderModel(),
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            invoice = invoice?.toInvoiceModel()
        )
    }
}
