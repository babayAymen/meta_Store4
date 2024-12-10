package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine
import com.aymen.metastore.model.entity.room.remoteKeys.OrderLineKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.store.model.Enum.Status
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseOrderLineDao {

    @Upsert
    suspend fun insert(order: List<PurchaseOrderLine>)

    suspend fun insertOrderLine(order: List<PurchaseOrderLine?>){
        order.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }
    @Upsert
    suspend fun insertOrderLineKeys(keys : List<OrderLineKeysEntity>)

    @Query("DELETE FROM order_line_keys")
    suspend fun clearOrderLineKeys()

    @Query("DELETE FROM purchase_order_line WHERE purchaseOrderId = :purchaseOrderId")
    suspend fun clearOrderLine(purchaseOrderId : Long)

    @Query("SELECT * FROM order_line_keys WHERE id = :id")
    suspend fun getAllOrderLineRemoteKeys(id : Long) : OrderLineKeysEntity
//
//    @Transaction
//    @Query("SELECT * FROM purchase_order_line WHERE id = :orderId")
//    fun getPurchaseOrderDetails(orderId : Long) : List<Flow<PurchaseOrderLineWithPurchaseOrderOrInvoice>>


    @Query("SELECT * FROM purchase_order_line")
    suspend fun getAllOrdersLine(): List<PurchaseOrderLine>

    @Query("SELECT * FROM purchase_order_line WHERE purchaseOrderId = :orderId")
     fun getAllMyOrdersLinesByOrderId(orderId : Long) : PagingSource<Int,PurchaseOrderLineWithPurchaseOrderOrInvoice>

     @Query("SELECT * FROM purchase_order_line WHERE invoiceId = :invoiceId")
     fun getAllMyOrdersLinesByInvoiceId(invoiceId : Long) : Flow<PurchaseOrderLine>

    @Query("UPDATE purchase_order_line SET status = :status WHERE purchaseOrderLineId = :id")
    suspend fun changeStatusByLine(status : Status , id : Long)

    @Query("UPDATE purchase_order_line SET status = :status WHERE purchaseOrderId = :id")
    suspend fun changeStatusByOrder(status : Status , id : Long)

    @Query("SELECT DISTINCT o.* FROM purchase_order_line AS l JOIN purchase_order AS o ON l.purchaseOrderId = o.purchaseOrderId WHERE l.status = :status")
     fun getAllMyOrdersNotAccepted( status : Status) : PagingSource<Int,PurchaseOrderWithCompanyAndUserOrClient>

     @Query("DELETE FROM purchase_order_line WHERE purchaseOrderId = :purchaseOrderId")
     suspend fun deleteByPurchaseOrderId(purchaseOrderId : Long)



}