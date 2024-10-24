package com.aymen.metastore.model.entity.room

import androidx.room.Embedded
import androidx.room.Relation

data class PurchaseOrderWithCompany(
    @Embedded val purchaseOrder: PurchaseOrder,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: Company,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Company? = null,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User? = null
)
