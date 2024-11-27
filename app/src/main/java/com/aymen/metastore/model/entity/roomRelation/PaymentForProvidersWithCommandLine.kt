package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.PaymentForProviders
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine

data class PaymentForProvidersWithCommandLine(

    @Embedded val paymentForProviders: PaymentForProviders,

    @Relation(
        entity = PurchaseOrderLine::class,
        parentColumn = "purchaseOrderLineId",
        entityColumn = "purchaseOrderLineId",
    )
    val purchaseOrderLine: PurchaseOrderLineWithPurchaseOrderOrInvoice
){
    fun toPaymentForProvidersWithCommandLine(): com.aymen.metastore.model.entity.model.PaymentForProviders {
        return paymentForProviders.toPaymentForProviders(
            purchaseOrderLine.toPurchaseOrderineWithPurchaseOrderOrinvoice()
        )
    }
}
