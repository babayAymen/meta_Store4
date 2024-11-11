package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.Rating
import com.aymen.metastore.model.entity.room.User

data class RatingWithRater(
    @Embedded val rating : Rating,

    @Relation(
        parentColumn = "raterUserId",
        entityColumn = "id"
    )
    val raterUser : User? = null,


    @Relation(
        parentColumn = "raterCompanyId",
        entityColumn = "id"
    )
    val raterCompany : Company? = null
)
