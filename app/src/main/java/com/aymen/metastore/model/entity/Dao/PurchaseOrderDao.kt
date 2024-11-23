package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.store.model.Enum.Status


@Dao
interface PurchaseOrderDao {

    @Upsert
    suspend fun insertOrder(order: List<PurchaseOrder>)


    @Upsert
    suspend fun insertOrderNotAcceptedKeys(keys : List<OrderNotAcceptedKeysEntity>)

    @Query("DELETE FROM order_not_accepted_keys_entity")
    suspend fun clearAllOrderNotAcceptedKeys()
    @Query("DELETE FROM purchase_order")
    suspend fun clearAllPurchaseOrderNotAccepted()
    @Query("SELECT * FROM order_not_accepted_keys_entity WHERE id = :id")
    suspend fun getAllOrderNotAccepteRemoteKeys(id : Long): OrderNotAcceptedKeysEntity

    @Transaction
    @Query("SELECT * FROM purchase_order WHERE (companyId = :id OR clientId = :id)  ORDER BY createdDate DESC")
     suspend fun getAllMyOrdersAsCompany(id : Long) : List<PurchaseOrderWithCompanyAndUserOrClient>


    @Query("SELECT * FROM purchase_order ORDER BY id DESC ")
    fun getAllMyOrdersNotAccepted(): PagingSource<Int, PurchaseOrderWithCompanyAndUserOrClient>

    @Transaction
    @Query("SELECT * FROM purchase_order WHERE userId = :id  ORDER BY createdDate DESC")
     suspend fun getAllMyOrdersAsUser(id : Long) :  List<PurchaseOrderWithCompanyAndUserOrClient>



}