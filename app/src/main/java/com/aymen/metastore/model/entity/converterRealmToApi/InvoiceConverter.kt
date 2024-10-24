package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.realm.Invoice
import io.realm.kotlin.internal.platform.returnType

fun mapInvoiceToInvoiceDto(invoice : Invoice): InvoiceDto{
    return InvoiceDto(
        id = invoice.id,
        code = invoice.code,
        tot_tva_invoice = invoice.tot_tva_invoice,
        prix_invoice_tot = invoice.prix_invoice_tot,
        prix_article_tot = invoice.prix_article_tot,
        discount = invoice.discount,
        status = Status.valueOf(invoice.status),
        person = invoice.person?.let { mapUserToUserDto(it) },
        client = invoice.client?.let { mapCompanyToCompanyDto(it) },
        provider = mapCompanyToCompanyDto(invoice.provider!!),
        paid = PaymentStatus.valueOf(invoice.paid),
        rest = invoice.rest,
        createdDate = invoice.createdDate,
        lastModifiedDate = invoice.lastModifiedDate,
        lastModifiedBy = invoice.lastModifiedBy,
        createdBy = invoice.createdBy,
        type = InvoiceDetailsType.valueOf(invoice.type!!)
    )
}

fun mapInvoiceToRealmInvoice(invoice : InvoiceDto): Invoice{
    return Invoice().apply {
        id = invoice.id
        code = invoice.code?: 0
        tot_tva_invoice = invoice.tot_tva_invoice
        prix_invoice_tot = invoice.prix_invoice_tot
        prix_article_tot = invoice.prix_article_tot
        discount = invoice.discount
        status = invoice.status.toString()
        person = invoice.person?.let { mapUserDtoToUserRealm(it) }
        client = invoice.client?.let { mapcompanyDtoToCompanyRealm(it) }
        provider = mapcompanyDtoToCompanyRealm(invoice.provider?:CompanyDto())
        paid = invoice.paid.toString()
        rest = invoice.rest
        createdDate = invoice.createdDate.toString()
        lastModifiedDate = invoice.lastModifiedDate.toString()
        lastModifiedBy = invoice.lastModifiedBy?:""
        createdBy = invoice.createdBy?:""
        type = invoice.type.toString()

    }
}

fun mapInvoiceToRoomInvoice(invoice: InvoiceDto?): com.aymen.metastore.model.entity.room.Invoice{
    return com.aymen.metastore.model.entity.room.Invoice(
        id = invoice?.id,
        code = invoice?.code,
        tot_tva_invoice = invoice?.tot_tva_invoice!!,
        prix_invoice_tot = invoice.prix_invoice_tot,
        prix_article_tot = invoice.prix_article_tot,
        discount = invoice.discount,
        status = invoice.status,
        personId = invoice.person?.id,
        clientId = invoice.client?.id,
        providerId = invoice.provider?.id,
        paid = invoice.paid,
        rest = invoice.rest,
        createdDate = invoice.createdDate,
        lastModifiedDate = invoice.lastModifiedDate,
        lastModifiedBy = invoice.lastModifiedBy,
        createdBy = invoice.createdBy,
        type = invoice.type
    )
}