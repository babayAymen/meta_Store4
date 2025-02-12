package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.model.Rating

@Entity(tableName = "rating",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["raterUserId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["rateeUserId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["raterCompanyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["rateeCompanyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = ArticleCompany::class, parentColumns = ["id"], childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ])
data class Rating(

    @PrimaryKey
    val id : Long? = null,
    val raterUserId: Long? = null,
    val rateeUserId: Long? = null,
    val raterCompanyId: Long? = null,
    val rateeCompanyId: Long? = null,
    val articleId : Long? = null,
    val comment: String? = null,
    val photo: String? = null,
    val type : RateType? = RateType.COMPANY_RATE_USER,
    val rateValue : Int? = 0,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
){
    fun toRating(raterUser : com.aymen.metastore.model.entity.model.User?,
                 rateeUser : com.aymen.metastore.model.entity.model.User?,
                 raterCompany : com.aymen.metastore.model.entity.model.Company?,
                 rateeCompany : com.aymen.metastore.model.entity.model.Company?,
                 article : com.aymen.metastore.model.entity.model.ArticleCompany?): Rating{
        return Rating(
            id = id,
            raterUser = raterUser,
            rateeUser = rateeUser,
            raterCompany = raterCompany,
            rateeCompany = rateeCompany,
            article = article,
            comment = comment,
            photo = photo,
            type = type,
            rateValue = rateValue,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,

        )
    }
}
