package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.remoteKeys.AllInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.BuyHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InCompleteRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesAsClientAndStatusRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.NotAcceptedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.NotPayedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PayedRemoteKeysEntity
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

     @Upsert
     fun insertBuyHistoryKeys(keys : List<BuyHistoryRemoteKeysEntity>)

     @Upsert
     fun insertBuyHistoryPaidKeys(keys : List<PayedRemoteKeysEntity>)

     @Upsert
     fun insertBuyHistoryNotPaidKeys(keys : List<NotPayedRemoteKeysEntity>)

     @Upsert
     fun insertBuyHistoryIncompleteKeys(keys : List<InCompleteRemoteKeysEntity>)

     @Upsert
     fun insertBuyHistoryNotAcceptedKeys(keys : List<NotAcceptedRemoteKeysEntity>)

     @Upsert
     fun insertAllInvoiceKeys(keys : List<AllInvoiceRemoteKeysEntity>)

     @Upsert
     fun insertInvoicesAsClientAndStatusKeys(keys : List<InvoicesAsClientAndStatusRemoteKeysEntity>)


     @Query("SELECT * FROM invoice_remote_keys_table WHERE id = :id")
     suspend fun getInvoiceRemoteKey(id : Long) : InvoiceRemoteKeysEntity

     @Query("SELECT * FROM buy_history_remote_keys_table WHERE id = :id")
     suspend fun getBuyHistoryRemoteKey(id : Long) : BuyHistoryRemoteKeysEntity

     @Query("SELECT * FROM `payed_remote-keys_entity` WHERE id = :id")
     suspend fun getBuyHistoryPaidRemoteKey(id : Long) : PayedRemoteKeysEntity

     @Query("SELECT * FROM not_payed_remote_keys_table WHERE id = :id")
     suspend fun getBuyHistoryNotPaidRemoteKey(id : Long) : NotPayedRemoteKeysEntity

     @Query("SELECT * FROM in_complete_remote_keys WHERE id = :id")
     suspend fun getBuyHistoryInCompleteRemoteKey(id : Long) : InCompleteRemoteKeysEntity

     @Query("SELECT * FROM not_accepted_remote_keys WHERE id = :id")
     suspend fun getBuyHistoryNotAcceptedRemoteKey(id : Long) : NotAcceptedRemoteKeysEntity

     @Query("SELECT * FROM all_invoice_remote_keys WHERE id = :id")
     suspend fun getAllInvoiceRemoteKey(id : Long) : AllInvoiceRemoteKeysEntity

     @Query("SELECT * FROM INVOICE_AS_CLIENT_AND_STATUS_REMOTE_KEYS WHERE id = :id")
     suspend fun getInvoiceAsClientAnStatusRemoteKey(id : Long) : InvoicesAsClientAndStatusRemoteKeysEntity

     @Query("Delete FROM all_invoice_remote_keys")
     suspend fun clearAllRemoteKeysTable()

     @Query("DELETE FROM invoice_remote_keys_table")
     suspend fun clearInvoiceRemoteKeysTable()


     @Query("DELETE FROM buy_history_remote_keys_table")
     suspend fun clearAllBuyHistoryRemoteKeysTable()

     @Query("DELETE FROM `payed_remote-keys_entity`")
     suspend fun clearAllBuyHistoryPaidRemoteKeysTable()

     @Query("DELETE FROM not_payed_remote_keys_table")
     suspend fun clearAllBuyHistoryNotPaidRemoteKeysTable()

     @Query("DELETE FROM in_complete_remote_keys")
     suspend fun clearAllBuyHistoryIncompleteRemoteKeysTable()

     @Query("DELETE FROM not_accepted_remote_keys")
     suspend fun clearAllBuyHistoryNotAcceptedRemoteKeysTable()

     @Query("DELETE FROM invoice WHERE (clientId = :id OR personId = :id)")
     suspend fun clearAllTableAsClient(id : Long)
     @Query("DELETE FROM invoice WHERE providerId = :id")
     suspend fun clearAllTableAsProvider(id : Long)

     @Query("DELETE FROM invoice WHERE paid = :paid")
     suspend fun clearAllBuyHistoryTableByPaidStatus(paid: PaymentStatus)

     @Query("DELETE FROM invoice WHERE status = :status")
     suspend fun clearAllBuyHistoryTableByStatus(status : Status)

     @Query("DELETE FROM invoice WHERE status = :status AND personId = :id")
     suspend fun clearAllInvoiceTableAsClientAnStatus(status : Status , id : Long)

     @Query("DELETE FROM invoice_as_client_and_status_remote_keys")
     suspend fun clearInvoicesAsClientAndStatusRemoteKeysTable()

     @Transaction
     @Query("SELECT * FROM invoice WHERE providerId = :companyId")
     fun getAllMyInvoiceAsProvider(companyId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE clientId = :clientId")
     fun getAllMyInvoiceAsClient(clientId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE personId = :clientId AND status = :status")
     fun getAllMyInvoiceAsClientAndStatus(clientId : Long, status : Status): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE  personId = :clientId")
     fun getAllMyInvoiceAsPersonClient(clientId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice ")
     fun getAllMyBuyHistory(): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid")
     fun getAllMyBuyHistoryFromPaidInvoice(paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>
     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid")
     fun getAllMyBuyHistoryFromNotPaidInvoice(paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>
     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid")
     fun getAllMyBuyHistoryFromIncompleteInvoice(paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>
     @Transaction
     @Query("SELECT * FROM invoice WHERE status = :status")
     fun getAllMyBuyHistoryFromNotAcceptedInvoice(status : Status) : PagingSource<Int, InvoiceWithClientPersonProvider>


}