package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.entity.room.entity.Invoice
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
    var person : User? = null,
    var client : Company? = null,
    var provider : Company? = null,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val lastModifiedBy: String? = null,
    val createdBy: String? = null,
    val isInvoice : Boolean? = false
){
    fun toInvoiceEntity() : Invoice {
        return Invoice(
            id = id,
            code = code,
            tot_tva_invoice = tot_tva_invoice,
            prix_invoice_tot = prix_invoice_tot,
            prix_article_tot = prix_article_tot,
            discount = discount,
            status = status,
            paid = paid,
            type = type,
            rest = rest,
            personId = person?.id,
            clientId = client?.id,
            providerId = provider?.id,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            lastModifiedBy = lastModifiedBy,
            createdBy = createdBy,
            isInvoice = isInvoice

            )
    }
}