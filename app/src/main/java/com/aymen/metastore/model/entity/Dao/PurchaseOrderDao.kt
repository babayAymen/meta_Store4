package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PurchaseOrder

@Dao
interface PurchaseOrderDao {

    @Upsert
    suspend fun insertOrder(order: PurchaseOrder)

    @Query("SELECT * FROM purchase_order")
    suspend fun getAllOrders(): List<PurchaseOrder>

//    @Transaction
//    fun insertpurchasewithuser(purchaseOrder: PurchaseOrder, user: com.aymen.metastore.model.entity.room.User){
//
//    }


}