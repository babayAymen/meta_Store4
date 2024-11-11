package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.dto.InvoiceDto

fun mapRoomInvoiceToInvoiceDto(invoice : com.aymen.metastore.model.entity.room.Invoice): InvoiceDto{
    return InvoiceDto(
        id = invoice.id,
        code = invoice.code,
        tot_tva_invoice = invoice.tot_tva_invoice,
        prix_invoice_tot = invoice.prix_invoice_tot,
        prix_article_tot = invoice.prix_article_tot,
        discount = invoice.discount,
        status = invoice.status!!,
//        person = invoice.personId?.let { mapUserToUserDto(it) },
//        client = invoice.clientId?.let { mapCompanyToCompanyDto(it) },
//        provider = mapCompanyToCompanyDto(invoice.provider!!),
        paid = invoice.paid!!,
        rest = invoice.rest,
        createdDate = invoice.createdDate,
        lastModifiedDate = invoice.lastModifiedDate,
        lastModifiedBy = invoice.lastModifiedBy,
        createdBy = invoice.createdBy,
        type = invoice.type!!
    )
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