package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PurchaseOrder
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import kotlinx.coroutines.flow.Flow


@Dao
interface PurchaseOrderDao {

    @Upsert
    suspend fun insertOrder(order: PurchaseOrder)

    @Query("SELECT * FROM purchase_order")
    suspend fun getAllOrders(): List<PurchaseOrder>


    @Query("SELECT * FROM purchase_order WHERE (companyId = :id OR clientId = :id)  ORDER BY createdDate DESC")
     suspend fun getAllMyOrdersAsCompany(id : Long) : List<PurchaseOrderWithCompanyAndUserOrClient>


    @Query("SELECT * FROM purchase_order WHERE userId = :id  ORDER BY createdDate DESC")
     suspend fun getAllMyOrdersAsUser(id : Long) :  List<PurchaseOrderWithCompanyAndUserOrClient>



}