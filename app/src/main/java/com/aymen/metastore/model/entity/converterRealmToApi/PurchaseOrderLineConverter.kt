package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.api.InvoiceDto
import com.aymen.store.model.entity.api.PurchaseOrderDto
import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.converterRealmToApi.mapApiArticleToRealm
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToDto
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRealm
import com.aymen.store.model.entity.converterRealmToApi.mapRealmArticleToApi
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.PurchaseOrderLine

fun mapPurchaseOrderLineDtoToRealm(purchaseOrderLine: PurchaseOrderLineDto): PurchaseOrderLine{
    return PurchaseOrderLine().apply {
         id = purchaseOrderLine.id

         article = mapArticleCompanyToRealm(purchaseOrderLine.article)

         quantity = purchaseOrderLine.quantity

         comment = purchaseOrderLine.comment?:""

         status = purchaseOrderLine.status.toString()

         delivery = purchaseOrderLine.delivery

         purchaseorder = mapPurchaseOrderDtoToPurchaseOrderRealm(purchaseOrderLine.purchaseorder)

         createdDate = purchaseOrderLine.createdDate

         lastModifiedDate = purchaseOrderLine.lastModifiedDate

        invoice = mapInvoiceToRealmInvoice(purchaseOrderLine.invoice?:InvoiceDto())
    }
}

fun mapPurchaseOrderRealmToDto(purchaseOrderLine: PurchaseOrderLine) : PurchaseOrderLineDto{
    return PurchaseOrderLineDto(
        id = purchaseOrderLine.id,

                    article = mapArticleCompanyToDto(purchaseOrderLine.article!!),

                quantity = purchaseOrderLine.quantity,

                comment = purchaseOrderLine.comment,

                status = Status.valueOf(purchaseOrderLine.status),

                delivery = purchaseOrderLine.delivery,

                purchaseorder = mapPuerchaseOrderRealmToPurchaseOrderDto(purchaseOrderLine.purchaseorder!!),

                createdDate = purchaseOrderLine.createdDate,

                lastModifiedDate = purchaseOrderLine.lastModifiedDate,

                invoice = mapInvoiceToInvoiceDto(purchaseOrderLine.invoice?:Invoice())
    )
}