package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status


data class Invoice (

    var code : Long? = null,
    val id : Long? = null,
    val tot_tva_invoice : Double = 0.0,
    val prix_invoice_tot : Double =0.0,
    val prix_article_tot : Double = 0.0,
    val discount : Double = 0.0,
    val status : Status? = Status.INWAITING,
    var paid : PaymentStatus? = PaymentStatus.NOT_PAID,
    val type : InvoiceDetailsType? = InvoiceDetailsType.COMMAND_LINE,
    var rest : Double = 0.0,
    val person : User? = null,
    val client : Company? = null,
    val provider : Company? = null,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val lastModifiedBy: String? = null,
    val createdBy: String? = null
)