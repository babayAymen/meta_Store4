package com.aymen.store.model.entity.dto

import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.store.model.Enum.Status

data class PurchaseOrderLineDto(

    var id : Long? = null,

    var article: ArticleCompanyDto? = null,

    var quantity: Double? = 0.0,

    var comment: String? = null,

    var status: Status? = Status.INWAITING,

    var delivery: Boolean? = false,

    var purchaseorder: PurchaseOrderDto? = null,

    var createdDate : String? = null,

    var lastModifiedDate : String? = null,

    var invoice : InvoiceDto? = null
)
