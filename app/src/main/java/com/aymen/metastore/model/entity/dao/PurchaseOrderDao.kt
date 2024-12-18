package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient


@Dao
interface PurchaseOrderDao {

    @Upsert
    suspend fun insert(order: List<PurchaseOrder>)

    suspend fun insertOrder(order : List<PurchaseOrder?>){
        order.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }

    @Upsert
    suspend fun insertOrderNotAcceptedKeys(keys : List<OrderNotAcceptedKeysEntity>)

    @Query("DELETE FROM order_not_accepted_keys_entity")
    suspend fun clearAllOrderNotAcceptedKeys()
    @Query("DELETE FROM purchase_order")
    suspend fun clearAllPurchaseOrderNotAccepted()
    @Query("SELECT * FROM order_not_accepted_keys_entity WHERE id = :id")
    suspend fun getAllOrderNotAccepteRemoteKeys(id : Long): OrderNotAcceptedKeysEntity


    @Query("SELECT * FROM purchase_order ORDER BY purchaseOrderId DESC ")
    fun getAllMyOrdersNotAccepted(): PagingSource<Int, PurchaseOrderWithCompanyAndUserOrClient>


     @Query("DELETE FROM purchase_order_line WHERE purchaseOrderId = :id")
     suspend fun deletePurchaseOrderById(id : Long)

     @Query("DELETE FROM purchase_order_line WHERE purchaseOrderLineId = :id")
     suspend fun deleteOrderNotAcceptedKeysById(id : Long)


}