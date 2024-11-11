package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.CommandLine
import com.aymen.metastore.model.entity.room.PaymentForProviders
import com.aymen.metastore.model.entity.room.PurchaseOrderLine

data class PaymentForProvidersWithCommandLine(

    @Embedded val paymentForProviders: PaymentForProviders,

    @Relation(
        parentColumn = "purchaseOrderLineId",
        entityColumn = "id"
    )
    val purchaseOrderLine: PurchaseOrderLine
)
