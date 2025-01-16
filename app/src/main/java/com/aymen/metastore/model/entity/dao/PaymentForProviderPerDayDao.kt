package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.room.entity.ReglementForProvider
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayByDateRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ReglementForProviderRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.entity.roomRelation.ReglementWithPaymentPerDay

@Dao
interface PaymentForProviderPerDayDao {

    @Upsert
    suspend fun insertPaymentForProviderPerDay(payment : List<PaymentForProviderPerDay>)

    suspend fun insertPaymentForProvider(payment : List<PaymentForProviderPerDay?>){
        payment.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insertPaymentForProviderPerDay(it)
            }
    }
    @Upsert
    fun insertPointsPaymentKeys(keys : List<PointsPaymentPerDayRemoteKeysEntity>)
    @Upsert
    fun insertReglementForProviderKeys(keys : List<ReglementForProviderRemoteKeys>)
    @Query("SELECT * FROM reglement_forProvider_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getFirstReglementRemoteKey() : ReglementForProviderRemoteKeys?
    @Query("SELECT * FROM reglement_forProvider_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getLatestReglementRemoteKey() : ReglementForProviderRemoteKeys?
    @Query("DELETE FROM reglement_forProvider_remote_keys")
    suspend fun clearAllReglementRemoteKeysTable()
    @Query("DELETE FROM reglement_for_provider")
    suspend fun clearAllReglementTable()


    @Transaction
    @Query("SELECT * FROM reglement_for_provider WHERE paymentForProviderPerDayId = :paymentId ORDER BY id DESC")
    fun getMyHistoryReglementForProvider(paymentId : Long) : PagingSource<Int , ReglementWithPaymentPerDay>


    @Upsert
    suspend fun insertPointsPaymentByDateKeys(keys : List<PointsPaymentPerDayByDateRemoteKeysEntity>)

    @Upsert
    suspend fun insertReglementForProvider(reglement : List<ReglementForProvider>)

    @Query("SELECT * FROM points_payment_per_day_remote_keys WHERE id = :id")
    suspend fun getPaymentForProviderPerDayRemoteKey(id : Long) : PointsPaymentPerDayRemoteKeysEntity

    @Query("SELECT * FROM points_payment_per_day_by_date_remote_keys_entity WHERE id = :id")
    suspend fun getPaymentForProviderPerDayByDateRemoteKey(id : Long) : PointsPaymentPerDayByDateRemoteKeysEntity

    @Query("DELETE FROM points_payment_per_day_remote_keys")
    suspend fun clearAllpaymentForProviderPerDayRemoteKeysTable()

    @Query("SELECT * FROM points_payment_per_day_remote_keys ORDER BY id ASC LIMIT 1")
    suspend fun getFirstPaymentForProviderPerDayRemoteKeys() : PointsPaymentPerDayRemoteKeysEntity?
    @Query("SELECT * FROM points_payment_per_day_remote_keys ORDER BY id DESC LIMIT 1")
    suspend fun getLatestPaymentForProviderPerDayRemoteKeys() : PointsPaymentPerDayRemoteKeysEntity?
    @Query("DELETE FROM points_payment_per_day_by_date_remote_keys_entity")
    suspend fun clearAllpaymentForProviderPerDayByDateRemoteKeysTable()

    @Query("SELECT * FROM points_payment_per_day_by_date_remote_keys_entity ORDER BY id ASC LIMIT 1")
    suspend fun getFirstAllPaymentForProviderRemoteKey() : PointsPaymentPerDayByDateRemoteKeysEntity?
    @Query("SELECT * FROM points_payment_per_day_by_date_remote_keys_entity ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAllPaymentForProviderRemoteKey() : PointsPaymentPerDayByDateRemoteKeysEntity?
    @Query("DELETE FROM payment_for_provider_per_day")
    suspend fun clearAllpaymentForProviderPerDayTable()

    @Transaction
    @Query("SELECT * FROM payment_for_provider_per_day ORDER BY lastModifiedDate DESC")
    fun getAllProfitPerDay() : PagingSource<Int, PaymentPerDayWithProvider>

    @Transaction
    @Query("SELECT * FROM payment_for_provider_per_day WHERE lastModifiedDate >= :beginDate AND lastModifiedDate <= :finalDate")
    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String) : PagingSource<Int, PaymentPerDayWithProvider>

    @Query("UPDATE payment_for_provider_per_day SET rest = :amount , isPayed = :isPayed WHERE id = :id")
    suspend fun updatePaymentForProviderPerDay(id : Long , amount : Double, isPayed : Boolean)

}