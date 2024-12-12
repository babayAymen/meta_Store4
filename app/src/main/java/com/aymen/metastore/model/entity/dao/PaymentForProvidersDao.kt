package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PaymentForProviders
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentForProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderProfitHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine

@Dao
interface PaymentForProvidersDao {

    @Upsert
    suspend fun insertPaymentForProviders(paymentForProviders: List<PaymentForProviders>)

    @Upsert
    suspend fun insertProviderProfitHistoryKeys(keys : List<ProviderProfitHistoryRemoteKeysEntity>)

    @Upsert
    fun insertKeys(keys : List<PointsPaymentForProviderRemoteKeysEntity>)

    @Query("SELECT * FROM points_payment_for_provider_remote_keys_entity WHERE id = :id")
    suspend fun getRemoteKeys(id : Long) : PointsPaymentForProviderRemoteKeysEntity?

    @Query("SELECT * FROM provider_profit_history_remote_keys WHERE id = :id")
    suspend fun getProvidersProfitHistoryRemoteKey(id : Long) : ProviderProfitHistoryRemoteKeysEntity

    @Query("SELECT count(*) FROM payment_for_providers")
    suspend fun existRecords() : Int

    @Transaction
    @Query("SELECT * FROM payment_for_providers WHERE lastModifiedDate BETWEEN :beginDate AND :finalDate")
     fun getAllMyPaymentsEspeceByDate( beginDate: String, finalDate: String) : PagingSource<Int, PaymentForProvidersWithCommandLine>

    @Query("DELETE FROM points_payment_for_provider_remote_keys_entity")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM provider_profit_history_remote_keys")
    suspend fun clearAllProvidersProfitHistoryRemoteKeysTable()

    @Query("SELECT purchaseOrderLineId FROM payment_for_providers ")
    suspend fun getAllProviderProfitIds(): List<Long>

    @Query("DELETE FROM payment_for_providers")
    suspend fun clearAllProvidersProfitHistoryTable()

    @Query("DELETE FROM points_payment")
    suspend fun clearPointsPayment()


    @Query("SELECT * FROM payment_for_providers WHERE createdDate BETWEEN :date AND :finDate")
    suspend fun getMyPaymentByDate(date: String , finDate : String): List<PaymentForProvidersWithCommandLine>

    @Transaction
    @Query("SELECT * from payment_for_providers")
    fun getAllMyPaymentsEspece(): PagingSource<Int, PaymentForProvidersWithCommandLine>

}