package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.remoteKeys.AllInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.BuyHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InCompleteRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesAsClientAndStatusRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesDeliveredRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.NotAcceptedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.NotPayedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PayedRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

@Dao
interface InvoiceDao {

    @Upsert
    suspend fun insert(invoice: List<Invoice>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertelse(invoices : List<Invoice>)

    suspend fun insertInvoice(invoice: List<Invoice?>){
     invoice.filterNotNull()
      .takeIf { it.isNotEmpty() }
      ?.let {
       insert(it)
      }
    }

 suspend fun insertInvoiceelse(invoice: List<Invoice?>){
  invoice.filterNotNull()
   .takeIf { it.isNotEmpty() }
   ?.let {
    insertelse(it)
   }
 }

 @Query("SELECT COUNT(*) FROM invoice WHERE isInvoice = :source")
 suspend fun getInvoiceCountBySource(source: Boolean): Int

 @Query("UPDATE invoice SET status = :newStatus WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: Long, newStatus: Status)

    @Upsert
     suspend fun insertKeys(keys: List<InvoiceRemoteKeysEntity>)

     @Query("DELETE FROM invoice_remote_keys_table WHERE id = :id")
     suspend fun deleteInvoiceRemoteKeyById(id: Long)

     @Query("DELETE FROM invoice WHERE id = :id")
     suspend fun deleteInvoiceById(id : Long)

     @Upsert
     suspend fun insertBuyHistoryKeys(keys : List<BuyHistoryRemoteKeysEntity>)

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

     @Query("SELECT * FROM invoice_remote_keys_table ORDER BY id ASC LIMIT 1")
     suspend fun getFirstInvoiceRemoteKey(): InvoiceRemoteKeysEntity?

     @Query("SELECT * FROM all_invoice_remote_keys ORDER BY id ASC LIMIT 1")
     suspend fun getFirstAllInvoiceRemoteKey(): AllInvoiceRemoteKeysEntity?


     @Query("SELECT * FROM invoice_remote_keys_table ORDER BY id DESC LIMIT 1")
     suspend fun getLatestInvoiceRemoteKey() : InvoiceRemoteKeysEntity?

     @Query("SELECT * FROM all_invoice_remote_keys ORDER BY id DESC LIMIT 1")
     suspend fun getLatestAllInvoiceRemoteKey() : AllInvoiceRemoteKeysEntity?

     @Query("Delete FROM all_invoice_remote_keys")
     suspend fun clearAllRemoteKeysTable()

     @Query("DELETE FROM invoice_remote_keys_table")
     suspend fun clearInvoiceRemoteKeysTable()


     @Query("DELETE FROM buy_history_remote_keys_table")
     suspend fun clearAllBuyHistoryRemoteKeysTable()

     @Query("SELECT * FROM buy_history_remote_keys_table ORDER BY id ASC LIMIT 1")
     suspend fun getFirstBuyHistoryRemoteKey(): BuyHistoryRemoteKeysEntity?
     @Query("SELECT * FROM buy_history_remote_keys_table ORDER BY id DESC LIMIT 1")
     suspend fun getLatestBuyHistoryRemoteKeys(): BuyHistoryRemoteKeysEntity?
     @Query("DELETE FROM `payed_remote-keys_entity`")
     suspend fun clearAllBuyHistoryPaidRemoteKeysTable()

     @Query("DELETE FROM not_payed_remote_keys_table")
     suspend fun clearAllBuyHistoryNotPaidRemoteKeysTable()

     @Query("DELETE FROM in_complete_remote_keys")
     suspend fun clearAllBuyHistoryIncompleteRemoteKeysTable()

     @Query("DELETE FROM not_accepted_remote_keys")
     suspend fun clearAllBuyHistoryNotAcceptedRemoteKeysTable()

     @Query("DELETE FROM invoice WHERE clientId = :id")
     suspend fun clearAllTableAsClient(id : Long)

     @Query("DELETE FROM invoice WHERE personId = :id")
     suspend fun clearAllTableAsPerson(id : Long)

     @Query("DELETE FROM invoice WHERE providerId = :id")
     suspend fun clearAllTableAsProvider(id : Long)

     @Query("DELETE FROM invoice WHERE providerId = :id AND status = :status")
     suspend fun clearAllTableAsProviderAndStatus(id : Long, status: PaymentStatus)

     @Query("DELETE FROM invoice WHERE paid = :paid")
     suspend fun clearAllBuyHistoryTableByPaidStatus(paid: PaymentStatus)

     @Query("DELETE FROM invoice WHERE status = :status")
     suspend fun clearAllBuyHistoryTableByStatus(status : Status)

     @Query("DELETE FROM invoice WHERE status = :status AND personId = :id")
     suspend fun clearAllInvoiceTableAsClientAnStatus(status : Status , id : Long)

     @Query("DELETE FROM invoice WHERE paid = :status AND clientId = :id")
     suspend fun clearAllInvoiceTableAsClientAndPaid(status : PaymentStatus , id : Long)

     @Query("DELETE FROM invoice WHERE paid = :status AND personId = :id")
     suspend fun clearAllInvoiceTableAsPersonAndPaid(status : PaymentStatus , id : Long)

     @Query("DELETE FROM invoice_as_client_and_status_remote_keys")
     suspend fun clearInvoicesAsClientAndStatusRemoteKeysTable()
     @Query("SELECT * FROM invoice_as_client_and_status_remote_keys ORDER BY id ASC LIMIT 1")
     suspend fun getFirstInvoiceAsClientRemoteKey() : InvoicesAsClientAndStatusRemoteKeysEntity?
     @Query("SELECT * FROM invoice_as_client_and_status_remote_keys ORDER BY id DESC LIMIT 1 ")
     suspend fun getLatestInvoiceAsClientRemoteKey() : InvoicesAsClientAndStatusRemoteKeysEntity?

     @Transaction
     @Query("SELECT * FROM invoice WHERE providerId = :companyId ORDER BY lastModifiedDate DESC")
     fun getAllMyInvoiceAsProvider(companyId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>

 @Transaction
     @Query("SELECT * FROM invoice WHERE providerId = :companyId AND paid = :status ORDER BY lastModifiedDate DESC")
     fun getAllMyInvoiceAsProviderAndStatus(companyId : Long, status: PaymentStatus): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE clientId = :clientId ORDER BY lastModifiedDate DESC")
     fun getAllMyInvoiceAsClient(clientId : Long): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE personId = :clientId AND status = :status ORDER BY lastModifiedDate DESC ")
     fun getAllMyInvoiceAsClientAndStatus(clientId : Long, status : Status): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE clientId = :clientId AND paid = :status  ORDER BY lastModifiedDate DESC")
     fun getAllMyInvoiceAsClientAndPaid(clientId : Long, status : PaymentStatus): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE personId = :clientId AND paid = :status ORDER BY lastModifiedDate DESC")
     fun getAllMyInvoiceAsPersonClientAndPaid(clientId : Long, status : PaymentStatus): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE  personId = :clientId AND isInvoice = :isInvoice ORDER BY lastModifiedDate DESC")
     fun getAllMyInvoiceAsPersonClient(clientId : Long, isInvoice : Boolean): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice ")
     fun getAllMyBuyHistory(): PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid AND providerId = :id")
     fun getAllMyBuyHistoryFromPaidInvoiceAsProvider(id : Long, paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>
     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid AND clientId = :id")
     fun getAllMyBuyHistoryFromPaidInvoiceAsClient(id : Long, paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid")
     fun getAllMyBuyHistoryFromNotPaidInvoice(paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid AND providerId = :id")
     fun getAllMyBuyHistoryFromIncompleteInvoice(id : Long, paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>

     @Transaction
     @Query("SELECT * FROM invoice WHERE paid = :paid AND clientId = :id")
     fun getAllMyBuyHistoryFromIncompleteInvoiceAsClient(id : Long , paid : PaymentStatus) : PagingSource<Int, InvoiceWithClientPersonProvider>


     @Transaction
     @Query("SELECT * FROM invoice WHERE status = :status")
     fun getAllMyBuyHistoryFromNotAcceptedInvoice(status : Status) : PagingSource<Int, InvoiceWithClientPersonProvider>

     @Query("UPDATE invoice SET paid = :paid , rest = :rest  WHERE id = :id")
     suspend fun updateInvoicePaidAndRest(id : Long, paid : PaymentStatus , rest : Double)

     @Query("DELETE FROM invoice")
     suspend fun clearAllTableInvoice()
}