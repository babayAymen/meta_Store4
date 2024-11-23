package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.User

data class CompanyWithCompanyClient(
    @Embedded var relation: ClientProviderRelation,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val clientUser: User? = null,

    @Relation(
        parentColumn = "userId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val clientCompany: CompanyWithUser?= null,

    @Relation(
        parentColumn = "userId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val provider: CompanyWithUser?= null,

){
    fun toCompanyWithCompanyClient(): com.aymen.metastore.model.entity.model.ClientProviderRelation {
        return relation.toClientProviderRelation(
            person = clientUser?.toUser(),
            provider = provider?.toCompany(),
            client = clientCompany?.toCompany()
        )
    }
}
