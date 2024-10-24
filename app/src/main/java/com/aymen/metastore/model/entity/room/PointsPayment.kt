package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "points_payment",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["providerId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["clientCompanyId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["clientUserId"]),
    ])
data class PointsPayment(

    @PrimaryKey
    val id : Long? = null,

    val amount: Long? = 0,

    val providerId: Long? = null,

    val clientCompanyId: Long? = null,

    val clientUserId: Long? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = ""
)
