package com.aymen.store.model.entity.api

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

data class InvoiceDto(

    var code : Long? = null,

    var id : Long? = null,

    var tot_tva_invoice : Double = 0.0,

    var prix_invoice_tot : Double =0.0,

    var prix_article_tot : Double = 0.0,

    var discount : Double = 0.0,

    var status : Status = Status.INWAITING,

    var paid : PaymentStatus = PaymentStatus.NOT_PAID,

    var type : InvoiceDetailsType? = InvoiceDetailsType.COMMAND_LINE,

    var rest : Double = 0.0,

    var person : UserDto? = null,

    var client : CompanyDto? = null,

    var provider : CompanyDto? = null,

    var createdDate : String? = null,

    var lastModifiedDate : String? = null,

    var lastModifiedBy: String? = null,

    var createdBy: String? = null
)
