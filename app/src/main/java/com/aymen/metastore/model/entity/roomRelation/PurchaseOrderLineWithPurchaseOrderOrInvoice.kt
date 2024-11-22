package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine

data class PurchaseOrderLineWithPurchaseOrderOrInvoice(
    @Embedded val purchaseOrderLine: PurchaseOrderLine,

    @Relation(
        parentColumn = "purchaseOrderId",
        entityColumn = "id",
        entity = PurchaseOrder::class
    )
    val purchaseOrder: PurchaseOrderWithCompanyAndUserOrClient? =null,

    @Relation(
        parentColumn = "invoiceId",
        entityColumn = "id",
        entity = Invoice::class
    )
    val invoice : InvoiceWithClientPersonProvider? = null,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    var article : ArticleWithArticleCompany? = null
){
    fun toPurchaseOrderineWithPurchaseOrderOrinvoice():com.aymen.metastore.model.entity.model.PurchaseOrderLine{
        return purchaseOrderLine.toPurchaseOrderLine(
            purchaseOrder?.toPurchaseOrderWithCompanyAndUserOrClient(),
            invoice?.toInvoiceWithClientPersonProvider(),
            article?.toArticleRelation()
        )
    }
}
