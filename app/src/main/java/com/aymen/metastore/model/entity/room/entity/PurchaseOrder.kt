package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PurchaseOrder

@Entity(tableName = "purchase_order",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index("companyId"), Index("clientId"), Index("userId")]
)
data class PurchaseOrder(

    @PrimaryKey(autoGenerate = false) val id: Long? = 0,
    val companyId: Long? = null,
    val clientId: Long? = null,
    val userId: Long? = null,
    val createdDate : String? = "",
    val orderNumber : Long? = 0
){
    fun toPurchaseOrder(company: com.aymen.metastore.model.entity.model.Company?,
                        client: com.aymen.metastore.model.entity.model.Company?,
                        user: com.aymen.metastore.model.entity.model.User?): PurchaseOrder {
        return PurchaseOrder(
            id = id,
            company = company,
            client = client,
            person = user,
            createdDate = createdDate,
            orderNumber = orderNumber
        )
    }
}

