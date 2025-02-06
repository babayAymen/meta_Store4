package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.store.model.Enum.Status

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

    @PrimaryKey(autoGenerate = false) val purchaseOrderId: Long? = null,
    val companyId: Long? = null,
    val clientId: Long? = null,
    val userId: Long? = null,
    val createdDate : String? = "",
    val orderNumber : Long? = 0,
    val isDelivered : Boolean? = false,
    val isTaken : Boolean? = false,
    val discount : Double? = null,
    val prix_order_tot : Double? = null,
    val tot_tva : Double? = null,
    val prix_article_tot : Double? = null,
    val status : Status? = Status.NULL,
    val deliveryCode : String? = null
){
    fun toPurchaseOrder(company: com.aymen.metastore.model.entity.model.Company?,
                        client: com.aymen.metastore.model.entity.model.Company?,
                        user: com.aymen.metastore.model.entity.model.User?): PurchaseOrder {
        return PurchaseOrder(
            id = purchaseOrderId,
            company = company,
            client = client,
            person = user,
            createdDate = createdDate,
            orderNumber = orderNumber,
            isDelivered = isDelivered,
            isTaken = isTaken,
            discount = discount,
            prix_order_tot = prix_order_tot,
            tot_tva = tot_tva,
            prix_article_tot = prix_article_tot,
            status = status,
            deliveryCode = deliveryCode
        )
    }
}

