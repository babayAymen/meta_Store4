package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Invoice
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

@Dao
interface InvoiceDao {

    @Upsert
    suspend fun insertInvoice(invoice: Invoice)

    @Query("SELECT * FROM invoice WHERE clientId = :clientId")
    suspend fun getInvoicesByClientId(clientId: Long): List<Invoice>

    @Query("UPDATE invoice SET status = :newStatus WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: Long, newStatus: Status)

    @Query("SELECT * FROM invoice WHERE providerId = :mycompanyId")
    suspend fun getAllInvoicesAsProvider(mycompanyId : Long) : List<InvoiceWithClientPersonProvider>


    @Query("SELECT * FROM invoice WHERE clientId = :mycompanyId AND status = :status")
    suspend fun getAllMyInvoicesAsClientAndStatus(mycompanyId : Long, status : Status) : List<InvoiceWithClientPersonProvider>

    @Query("SELECT * FROM invoice WHERE personId = :userId AND status = :status")
    suspend fun getAllMyInvoicesAsPersonAndStatus(userId : Long, status : Status) : List<InvoiceWithClientPersonProvider>

    @Query("SELECT * FROM invoice WHERE providerId = :mycompanyId AND status = :status")
    suspend fun getAllMyInvoicesAsProviderAndStatus(mycompanyId : Long, status : Status) : List<InvoiceWithClientPersonProvider>

    @Query("SELECT * FROM invoice WHERE providerId = :mycompanyId AND paid = :status")
    suspend fun getAllInvoicesAsProviderAndPaymentStatus(mycompanyId : Long, status : PaymentStatus) : List<InvoiceWithClientPersonProvider>


}