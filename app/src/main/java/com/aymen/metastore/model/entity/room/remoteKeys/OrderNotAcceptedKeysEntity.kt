package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine

@Entity(tableName = "order_not_accepted_keys_entity",
//    foreignKeys = [ForeignKey(entity = PurchaseOrder::class, parentColumns = ["purchaseOrderId"], childColumns = ["id"],
//        onDelete = ForeignKey.CASCADE,
//        onUpdate = ForeignKey.CASCADE),]
    )
data class OrderNotAcceptedKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val prevPage: Int?,
    val nextPage: Int?
)
