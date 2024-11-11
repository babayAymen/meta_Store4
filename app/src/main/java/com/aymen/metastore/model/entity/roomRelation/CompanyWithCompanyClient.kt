package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.ClientProviderRelation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.User

data class CompanyWithCompanyClient(
    @Embedded val relation: ClientProviderRelation,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val clientCompany: Company? = null,
    @Relation(
        parentColumn = "providerId",
        entityColumn = "id"
    )
    val provider: Company,

    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val clientUser: User? = null

)
