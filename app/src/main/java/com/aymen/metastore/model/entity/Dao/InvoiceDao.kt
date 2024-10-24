package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Invoice
import com.aymen.store.model.Enum.Status

@Dao
interface InvoiceDao {

    @Upsert
    suspend fun insertInvoice(invoice: Invoice)

    @Query("SELECT * FROM invoice WHERE clientId = :clientId")
    suspend fun getInvoicesByClientId(clientId: Long): List<Invoice>

    @Query("UPDATE invoice SET status = :newStatus WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: Long, newStatus: Status)
}