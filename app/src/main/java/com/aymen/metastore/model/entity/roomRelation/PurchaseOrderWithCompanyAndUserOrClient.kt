package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.PurchaseOrder
import com.aymen.metastore.model.entity.room.User

data class PurchaseOrderWithCompanyAndUserOrClient(
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
    val person: User? = null

)
