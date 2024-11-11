package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.Invoice
import com.aymen.metastore.model.entity.room.User

data class InvoiceWithClientPersonProvider(
    @Embedded val invoice: Invoice,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client : Company? = null,

    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person : User? = null,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "id"
    )
    val provider : Company
)
