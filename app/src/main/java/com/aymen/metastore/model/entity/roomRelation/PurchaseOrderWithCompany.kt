package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.User

data class PurchaseOrderWithCompany(
    @Embedded val purchaseOrder: PurchaseOrder,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "id",
        entity = Company::class
    )
    val company: CompanyWithUser,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id",
        entity = Company::class
    )
    val client: CompanyWithUser? = null,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User? = null
){
    fun toPurchaseOrder(): com.aymen.metastore.model.entity.model.PurchaseOrder{
        return purchaseOrder.toPurchaseOrder(
            company = company.toCompany()!!,
            client = client?.toCompany(),
            user = user?.toUser()
        )
    }
}
