package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.store.model.Enum.Status

@Dao
interface PurchaseOrderLineDao {

    @Upsert
    suspend fun insertOrderLine(order: PurchaseOrderLine)

    @Query("SELECT * FROM purchase_order_line")
    suspend fun getAllOrdersLine(): List<PurchaseOrderLine>

    @Query("SELECT * FROM purchase_order_line WHERE purchaseOrderId = :orderId")
    suspend fun getAllMyOrdersLinesByOrderId(orderId : Long) : List<PurchaseOrderLineWithPurchaseOrderOrInvoice>

    @Query("UPDATE purchase_order_line SET status = :status WHERE id = :id")
    suspend fun changeStatusByLine(status : Status , id : Long)

    @Query("UPDATE purchase_order_line SET status = :status WHERE purchaseOrderId = :id")
    suspend fun changeStatusByOrder(status : Status , id : Long)
}