package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.DeliveryCategory

@Entity(tableName = "delivery",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ])
data class Delivery(

    @PrimaryKey
    val id : Long? = null,
    val user : Long? = null,
    val rate : Long,
    val category : DeliveryCategory? = DeliveryCategory.MEDIUM,
    val createdDate : String = "",
    val lastModifiedDate : String = ""
){
    fun toDelivery(user : com.aymen.metastore.model.entity.model.User): com.aymen.metastore.model.entity.model.Delivery {
        return com.aymen.metastore.model.entity.model.Delivery(
            id = id,
            user = user,
            rate = rate,
            category = category,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
