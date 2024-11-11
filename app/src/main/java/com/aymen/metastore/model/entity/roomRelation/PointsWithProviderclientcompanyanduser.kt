package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.PointsPayment
import com.aymen.metastore.model.entity.room.User

data class PointsWithProviderclientcompanyanduser(
    @Embedded val pointsPayment: PointsPayment,

    @Relation(
        parentColumn = "clientCompanyId",
        entityColumn = "id"
    )
    val client : Company? = null,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "id"
    )
    val provider : Company,

    @Relation(
        parentColumn = "clientUserId",
        entityColumn = "id"
    )
    val person : User? = null
)
