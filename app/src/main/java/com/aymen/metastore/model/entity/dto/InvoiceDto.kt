package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

data class InvoiceDto(

    var code : Long? = null,
    val id : Long? = null,
    val tot_tva_invoice : Double? = 0.0,
    val prix_invoice_tot : Double? =0.0,
    val prix_article_tot : Double? = 0.0,
    val discount : Double? = 0.0,
    val status : Status? = Status.NULL,
    var paid : PaymentStatus? = PaymentStatus.NOT_PAID,
    val type : InvoiceDetailsType? = InvoiceDetailsType.COMMAND_LINE,
    var rest : Double? = 0.0,
    val person : UserDto? = null,
    val client : CompanyDto? = null,
    val provider : CompanyDto? = null,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val lastModifiedBy: String? = null,
    val createdBy: String? = null,
    val purchaseOrder : PurchaseOrderDto? = null,
    val asProvider : Boolean? = true
){
    fun toInvoice(isInvoice : Boolean) : Invoice {

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

    fun toInvoiceModel() : com.aymen.metastore.model.entity.model.Invoice {
        return com.aymen.metastore.model.entity.model.Invoice(
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
            person = person?.toUserModel(),
            client = client?.toCompanyModel(),
            provider = provider?.toCompanyModel(),
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            lastModifiedBy = lastModifiedBy,
            createdBy = createdBy,
            purchaseOrder = purchaseOrder?.toPurchaseOrderModel(),
            asProvider =  asProvider
        )
    }
}
