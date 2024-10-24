package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.RateType
@Entity(tableName = "rating",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["raterUserId"]),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["rateeUserId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["raterCompanyId"]),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["rateeCompanyId"]),
    ])
data class Rating(

    @PrimaryKey
    val id : Long? = null,

    val raterUserId: Long? = null,
    
    val rateeUserId: Long? = null,

    val raterCompanyId: Long? = null,

    val rateeCompanyId: Long? = null,

    val comment: String? = null,

    val photo: String? = null,

    val type : RateType? = RateType.COMPANY_RATE_USER,

    val rateValue : Int? = 0,

    val createdDate : String? = "",

    val lastModifiedDate : String? = ""
)
