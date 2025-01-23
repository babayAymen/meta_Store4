package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.PaymentForProviders
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine

data class PaymentForProvidersWithCommandLine(

    @Embedded val paymentForProviders: PaymentForProviders,

    @Relation(
        entity = PurchaseOrder::class,
        parentColumn = "purchaseOrderId",
        entityColumn = "purchaseOrderId",
    )
    val purchaseOrder: PurchaseOrderWithCompanyAndUserOrClient
){
    fun toPaymentForProvidersWithCommandLine(): com.aymen.metastore.model.entity.model.PaymentForProviders {
        return paymentForProviders.toPaymentForProviders(
            purchaseOrder.toPurchaseOrderWithCompanyAndUserOrClient()
        )
    }
}
