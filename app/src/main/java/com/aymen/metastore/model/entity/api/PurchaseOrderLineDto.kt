package com.aymen.store.model.entity.api

import com.aymen.metastore.model.entity.api.ArticleCompanyDto
import com.aymen.store.model.Enum.Status

data class PurchaseOrderLineDto(

    var id : Long? = null,

    var article: ArticleCompanyDto = ArticleCompanyDto(),

    var quantity: Double = 0.0,

    var comment: String? = "",

    var status: Status = Status.INWAITING,

    var delivery: Boolean = false,

    var purchaseorder: PurchaseOrderDto = PurchaseOrderDto(),

    var createdDate : String = "",

    var lastModifiedDate : String = "",

    var invoice : InvoiceDto? = null
)
