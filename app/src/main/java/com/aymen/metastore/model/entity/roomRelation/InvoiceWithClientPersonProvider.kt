package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.entity.User

data class InvoiceWithClientPersonProvider(
    @Embedded val invoice: Invoice,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "userId",
        entity = Company::class
    )
    val client : CompanyWithUser? = null,

    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person : User? = null,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "userId",
        entity = Company::class
    )
    val provider : CompanyWithUser
){
    fun toInvoiceWithClientPersonProvider():com.aymen.metastore.model.entity.model.Invoice{
        return invoice.toInvoice(
            user = person?.toUser(),
            client = client?.toCompany(),
            provider = provider.toCompany()
        )
    }
}