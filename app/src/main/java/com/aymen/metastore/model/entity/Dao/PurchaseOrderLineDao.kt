package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PurchaseOrderLine
import com.aymen.store.model.entity.dto.PurchaseOrderDto

@Dao
interface PurchaseOrderLineDao {

    @Upsert
    suspend fun insertOrderLine(order: PurchaseOrderLine)

    @Query("SELECT * FROM purchase_order_line")
    suspend fun getAllOrdersLine(): List<PurchaseOrderLine>
}