package com.aymen.metastore.model.entity.converterRealmToApi

import android.util.Log
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRealm
import com.aymen.store.model.entity.realm.PurchaseOrderLine

fun mapPurchaseOrderLineDtoToRealm(purchaseOrderLine: PurchaseOrderLineDto): PurchaseOrderLine{
    return PurchaseOrderLine().apply {
         id = purchaseOrderLine.id

         article = mapArticleCompanyToRealm(purchaseOrderLine.article!!)

         quantity = purchaseOrderLine.quantity!!

         comment = purchaseOrderLine.comment?:""

         status = purchaseOrderLine.status.toString()

         delivery = purchaseOrderLine.delivery!!

         purchaseorder = mapPurchaseOrderDtoToPurchaseOrderRealm(purchaseOrderLine.purchaseorder!!)

         createdDate = purchaseOrderLine.createdDate!!

         lastModifiedDate = purchaseOrderLine.lastModifiedDate!!

        invoice = mapInvoiceToRealmInvoice(purchaseOrderLine.invoice?:InvoiceDto())
    }
}

fun mapPurchaseOrderLineToRoomPurchaseOrderLine(purchaseOrderLine: PurchaseOrderLineDto) : com.aymen.metastore.model.entity.room.PurchaseOrderLine{
        Log.e("mapPurchaseOrderLineToRoomPurchaseOrderLine","fin invoice id ${purchaseOrderLine.invoice?.id}")
    return com.aymen.metastore.model.entity.room.PurchaseOrderLine(
        id = purchaseOrderLine.id,

                quantity = purchaseOrderLine.quantity,

                comment = purchaseOrderLine.comment,

                status = purchaseOrderLine.status,

                delivery = purchaseOrderLine.delivery,

                purchaseOrderId = purchaseOrderLine.purchaseorder?.id,

                createdDate = purchaseOrderLine.createdDate,

                lastModifiedDate = purchaseOrderLine.lastModifiedDate,

                invoiceId = purchaseOrderLine.invoice?.id,
    )
}
