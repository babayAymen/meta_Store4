package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Payment
import com.aymen.metastore.model.entity.room.remoteKeys.PaymentRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.PaymentWithInvoice

@Dao
interface PaymentDao {

    @Upsert
    suspend fun insertPayment(payments : List<Payment>)

    @Upsert
    suspend fun insertKeys(keys : List<PaymentRemoteKeys>)

    @Query("SELECT * FROM payment_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getFirstPaymentRemoteKey(): PaymentRemoteKeys?

    @Query("SELECT * FROM payment_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getLatestPaymentRemoteKey() : PaymentRemoteKeys?

    @Query("SELECT id FROM payment WHERE invoiceId = :invoiceId")
    suspend fun getPaymentIdsByInvoiceId(invoiceId: Long): List<Long>

    @Query("DELETE FROM payment WHERE invoiceId = :invoiceId")
    suspend fun deletePaymentsByInvoiceId(invoiceId: Long)

    @Query("DELETE FROM payment_remote_keys WHERE id = :id")
    suspend fun clearAllRemoteKeysTableById(id : Long)
    @Transaction
    @Query("SELECT * FROM payment WHERE invoiceId = :invoiceId ORDER BY id DESC ")
    fun getPaymentHystoricByInvoiceId(invoiceId : Long): PagingSource<Int,PaymentWithInvoice>
    @Query("SELECT COUNT(*) FROM payment_remote_keys")
    suspend fun getPaymentRemoteKeysCount() : Int
    @Query("DELETE FROM payment WHERE id = :id")
    suspend fun deletePaymentById(id : Long)
}


// SOUMAIYA