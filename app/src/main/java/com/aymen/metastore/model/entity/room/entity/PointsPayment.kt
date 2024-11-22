package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PointsPayment

@Entity(tableName = "points_payment",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["clientCompanyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["clientUserId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ],
    indices = [Index("providerId"), Index("clientCompanyId"), Index("clientUserId")]
)
data class PointsPayment(

    @PrimaryKey
    val id : Long? = null,
    val amount: Long? = 0,
    val providerId: Long? = null,
    val clientCompanyId: Long? = null,
    val clientUserId: Long? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = ""
){
    fun toPointsPayment(provider : com.aymen.metastore.model.entity.model.Company,
                        clientCompany : com.aymen.metastore.model.entity.model.Company?,
                        clientUser : com.aymen.metastore.model.entity.model.User?): PointsPayment{
        return PointsPayment(
            id = id,
            amount = amount,
            provider = provider,
            clientCompany = clientCompany,
            clientUser = clientUser,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
