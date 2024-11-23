package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.User

data class PurchaseOrderWithCompanyAndUserOrClient(
    @Embedded val purchaseOrder: PurchaseOrder,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val company: CompanyWithUser?,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val client: CompanyWithUser? = null,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val person: User? = null

){
    fun toPurchaseOrderWithCompanyAndUserOrClient(): com.aymen.metastore.model.entity.model.PurchaseOrder{
        return purchaseOrder.toPurchaseOrder(
            company = company?.toCompany(),
            client = client?.toCompany(),
            user = person?.toUser()
        )
    }
}
