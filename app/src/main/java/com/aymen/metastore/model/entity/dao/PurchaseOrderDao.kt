package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesDeliveredRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PurchaseOrderRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.store.model.Enum.Status


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
    @Query("SELECT * FROM order_not_accepted_keys_entity ORDER BY id ASC LIMIT 1")
    suspend fun getFirstAllPurchaseOrderNotAcceptedKey() : OrderNotAcceptedKeysEntity?
    @Query("SELECT * FROM order_not_accepted_keys_entity ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAllOrderNotAcceptedKey() : OrderNotAcceptedKeysEntity?
    @Query("DELETE FROM purchase_order")
    suspend fun clearAllPurchaseOrderNotAccepted()
    @Query("SELECT * FROM order_not_accepted_keys_entity WHERE id = :id")
    suspend fun getAllOrderNotAccepteRemoteKeys(id : Long): OrderNotAcceptedKeysEntity

     @Query("DELETE FROM purchase_order_line WHERE purchaseOrderId = :id")
     suspend fun deletePurchaseOrderById(id : Long)

     @Query("DELETE FROM purchase_order_line WHERE purchaseOrderLineId = :id")
     suspend fun deleteOrderNotAcceptedKeysById(id : Long)

     @Query("DELETE FROM purchase_order WHERE purchaseOrderId = :id")
     suspend fun deleteByPurchaseOrderId(id : Long)

     @Query("SELECT * FROM order_not_accepted_keys_entity ORDER BY id DESC LIMIT 1")
     suspend fun getLatestOrderRemoteKey() : OrderNotAcceptedKeysEntity?
    @Query("SELECT COUNT(*) FROM order_not_accepted_keys_entity")
    suspend fun getOrderCount(): Int

    @Upsert
    suspend fun insertPurchaseOrderRemoteKeys(keys : List<PurchaseOrderRemoteKeys>)

    @Query("SELECT * FROM purchase_order_remote_key ORDER BY id ASC LIMIT 1")
    suspend fun getFirstPurchaseOrderRemoteKey() : PurchaseOrderRemoteKeys?
    @Query("SELECT * FROM purchase_order_remote_key ORDER BY id DESC LIMIT 1")
    suspend fun getLatestPurchaseOrderRemoteKey() : PurchaseOrderRemoteKeys?
    @Query("DELETE FROM purchase_order")
    suspend fun clearAllTablePurchaseOrder()
    @Query("DELETE FROM purchase_order_remote_key")
    suspend fun clearAllRemoteKeysTable()
    @Query("DELETE FROM purchase_order WHERE isDelivered = :isDelivered")
    suspend fun clearOrdersDelivered(isDelivered : Boolean)

    @Query("DELETE FROM invoices_delivered_remote_keys")
    suspend fun clearInvoicesDeliveredRemoteKeysTable()
    @Transaction
    @Query("SELECT * FROM purchase_order where isTaken = :isTaken")
    fun getInvoicesDelivered(isTaken : Boolean) : PagingSource<Int, PurchaseOrderWithCompanyAndUserOrClient>
    @Query("UPDATE purchase_order SET isTaken = :isTaken WHERE purchaseOrderId = :invoiceId")
    suspend fun makeInvoiceAsTeken(isTaken: Boolean, invoiceId: Long)
    @Upsert
    fun insertInvoicesDeliveredKeys(keys : List<InvoicesDeliveredRemoteKeysEntity>)

    @Query("SELECT * FROM invoices_delivered_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getFirstInvoicesDeliveredRemoteKey(): InvoicesDeliveredRemoteKeysEntity?

    @Query("SELECT * FROM invoices_delivered_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getLatestInvoicesDeliveredRemoteKey() : InvoicesDeliveredRemoteKeysEntity?

}












