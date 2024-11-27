package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.User

data class CompanyWithCompanyOrUser (
    @Embedded
    val relation : ClientProviderRelation,

    @Relation(
        entity = Company::class,
        parentColumn = "clientId",
        entityColumn = "companyId"
    )
    val clientCompany: CompanyWithUser?,

    @Relation(
        entity = Company::class,
        parentColumn = "providerId",
        entityColumn = "companyId"
    )
    val provider: CompanyWithUser?,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val clienUser : User?
    ){
        fun toClientProviderRelation(): com.aymen.metastore.model.entity.model.ClientProviderRelation {
            return relation.toClientProviderRelation(
                person = clienUser?.toUser(),
                provider = provider?.toCompany(),
                client = clientCompany?.toCompany()
            )
        }
}