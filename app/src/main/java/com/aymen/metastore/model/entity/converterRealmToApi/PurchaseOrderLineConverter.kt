package com.aymen.metastore.model.entity.converterRealmToApi

import android.util.Log
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto

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
