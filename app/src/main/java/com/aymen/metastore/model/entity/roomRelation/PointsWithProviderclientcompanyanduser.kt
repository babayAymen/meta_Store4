package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.PointsPayment
import com.aymen.metastore.model.entity.room.entity.User

data class PointsWithProviderclientcompanyanduser(
    @Embedded val pointsPayment: PointsPayment,

    @Relation(
        parentColumn = "clientCompanyId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val client : CompanyWithUser? = null,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val provider : CompanyWithUser,

    @Relation(
        parentColumn = "clientUserId",
        entityColumn = "id"
    )
    val person : User? = null
){
    fun toPointsWithProvidersClientCompanyAndUser():com.aymen.metastore.model.entity.model.PointsPayment{
        return pointsPayment.toPointsPayment(
            provider = provider.toCompany()!!,
            clientCompany = client?.toCompany(),
            clientUser = person?.toUser()
        )
    }
}
