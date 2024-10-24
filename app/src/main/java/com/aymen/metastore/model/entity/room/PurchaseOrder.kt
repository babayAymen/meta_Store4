package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_order",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companyId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["clientId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class PurchaseOrder(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val companyId: Long? = null,

    val clientId: Long? = null,

    val userId: Long? = null,

    val createdDate : String? = "",

    val orderNumber : Long? = 0L

)

