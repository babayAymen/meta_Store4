package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.DeliveryCategory

@Entity(tableName = "delivery",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
    ])
data class Delivery(

    @PrimaryKey
    val id : Long? = null,

    val user : Long? = null,

    val rate : Long,

    val category : DeliveryCategory? = DeliveryCategory.MEDIUM
)
