package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status


data class Invoice (

    var code : Long? = null,
    val id : Long? = null,
    var tot_tva_invoice : Double? = 0.0,
    var prix_invoice_tot : Double? =0.0,
    var prix_article_tot : Double? = 0.0,
    var discount : Double? = 0.0,
    val status : Status? = Status.INWAITING,
    var paid : PaymentStatus? = PaymentStatus.NOT_PAID,
    var type : InvoiceDetailsType? = InvoiceDetailsType.COMMAND_LINE,
    var rest : Double? = 0.0,
    var person : User? = null,
    var client : Company? = null,
    var provider : Company? = null,
    var createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val lastModifiedBy: String? = null,
    val createdBy: String? = null,
    val isInvoice : Boolean? = false,
    val purchaseOrder : PurchaseOrder? = null,
    val asProvider : Boolean? = true
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
            isInvoice = isInvoice,
            purchaseOrderId = purchaseOrder?.id,
            asProvider = asProvider

            )
    }

    fun toInvoiceDto() : InvoiceDto{
        return InvoiceDto(
            code,
            id,
            tot_tva_invoice,
            prix_invoice_tot,
            prix_article_tot,
            discount,
            status,
            paid,
            type,
            rest,
            person = person?.toUserDto(),
            client = client?.toCompanyDto(),
            provider = provider?.toCompanyDto(),
            createdDate,
            lastModifiedDate,
            createdBy,
            lastModifiedBy,
            purchaseOrder = purchaseOrder?.toPurchaseOrderDto(),
            asProvider = asProvider
        )
    }
}