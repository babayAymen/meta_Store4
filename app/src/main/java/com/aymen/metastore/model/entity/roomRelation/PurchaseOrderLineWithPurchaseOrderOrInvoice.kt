package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.metastore.model.entity.room.Invoice
import com.aymen.metastore.model.entity.room.PurchaseOrder
import com.aymen.metastore.model.entity.room.PurchaseOrderLine

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
        entityColumn = "id"
    )
    val invoice : Invoice? = null,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    var article : ArticleWithArticleCompany? = null
)
