package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

@Dao
interface InvoiceDao {

    @Upsert
    suspend fun insertInvoice(invoice: List<Invoice>)

    @Query("SELECT * FROM invoice WHERE clientId = :clientId")
    suspend fun getInvoicesByClientId(clientId: Long): List<Invoice>

    @Query("UPDATE invoice SET status = :newStatus WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: Long, newStatus: Status)

    @Transaction
    @Query("SELECT * FROM invoice WHERE providerId = :mycompanyId")
    suspend fun getAllInvoicesAsProvider(mycompanyId : Long) : List<InvoiceWithClientPersonProvider>

    @Transaction
    @Query("SELECT * FROM invoice WHERE clientId = :mycompanyId AND status = :status")
    suspend fun getAllMyInvoicesAsClientAndStatus(mycompanyId : Long, status : Status) : List<InvoiceWithClientPersonProvider>

    @Transaction
    @Query("SELECT * FROM invoice WHERE personId = :userId AND status = :status")
    suspend fun getAllMyInvoicesAsPersonAndStatus(userId : Long, status : Status) : List<InvoiceWithClientPersonProvider>

    @Transaction
    @Query("SELECT * FROM invoice WHERE providerId = :mycompanyId AND status = :status")
    suspend fun getAllMyInvoicesAsProviderAndStatus(mycompanyId : Long, status : Status) : List<InvoiceWithClientPersonProvider>

    @Transaction
    @Query("SELECT * FROM invoice WHERE providerId = :mycompanyId AND paid = :status")
    suspend fun getAllInvoicesAsProviderAndPaymentStatus(mycompanyId : Long, status : PaymentStatus) : List<InvoiceWithClientPersonProvider>

    @Upsert
     fun insertKeys(keys: List<InvoiceRemoteKeysEntity>)

     @Query("SELECT * FROM invoice_remote_keys_table WHERE id = :id")
     suspend fun getInvoiceRemoteKey(id : Long) : InvoiceRemoteKeysEntity

     @Query("Delete FROM invoice_remote_keys_table")
     suspend fun clearAllRemoteKeysTable()

     @Query("DELETE FROM invoice")
     suspend fun clearAllTable()

     @Transaction
     @Query("SELECT * FROM invoice WHERE providerId = :companyId")
     fun getAllMyInvoiceAsProvider(companyId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE (clientId = :clientId) OR (personId = :clientId)")
     fun getAllMyInvoiceAsClient(clientId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>




}