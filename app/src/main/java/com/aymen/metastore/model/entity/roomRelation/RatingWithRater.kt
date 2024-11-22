package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.Rating
import com.aymen.metastore.model.entity.room.entity.User

data class RatingWithRater(
    @Embedded val rating : Rating,

    @Relation(
        parentColumn = "raterUserId",
        entityColumn = "id"
    )
    val raterUser : User? = null,
    @Relation(
        parentColumn = "rateeUserId",
        entityColumn = "id"
    )
    val rateeUser : User? = null,

    @Relation(
        parentColumn = "rateeCompanyId",
        entityColumn = "userId",
        entity = Company::class
    )
    val rateeCompany : CompanyWithUser? = null,

    @Relation(
        parentColumn = "raterCompanyId",
        entityColumn = "userId",
        entity = Company::class
    )
    val raterCompany : CompanyWithUser? = null
){
    fun toRatingWithRater():com.aymen.metastore.model.entity.model.Rating{
        return rating.toRating(
            rateeCompany = rateeCompany?.toCompany(),
            raterCompany = raterCompany?.toCompany(),
            rateeUser = rateeUser?.toUser(),
            raterUser = raterUser?.toUser()
        )
    }
}