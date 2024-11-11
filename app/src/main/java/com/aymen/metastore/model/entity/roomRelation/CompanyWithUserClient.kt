package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.ClientProviderRelation
import com.aymen.metastore.model.entity.room.User

data class CompanyWithUserClient(
    @Embedded val relation: ClientProviderRelation,

    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val client: User?
)
