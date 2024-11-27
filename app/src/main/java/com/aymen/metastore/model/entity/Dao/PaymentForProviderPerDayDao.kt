package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayByDateRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider

@Dao
interface PaymentForProviderPerDayDao {

    @Upsert
    suspend fun insertPaymentForProviderPerDay(payment : List<PaymentForProviderPerDay>)
    @Upsert
    fun insertPointsPaymentKeys(keys : List<PointsPaymentPerDayRemoteKeysEntity>)

    @Upsert
    suspend fun insertPointsPaymentByDateKeys(keys : List<PointsPaymentPerDayByDateRemoteKeysEntity>)

    @Query("SELECT * FROM points_payment_per_day_remote_keys WHERE id = :id")
    suspend fun getPaymentForProviderPerDayRemoteKey(id : Long) : PointsPaymentPerDayRemoteKeysEntity

    @Query("SELECT * FROM points_payment_per_day_by_date_remote_keys_entity WHERE id = :id")
    suspend fun getPaymentForProviderPerDayByDateRemoteKey(id : Long) : PointsPaymentPerDayByDateRemoteKeysEntity

    @Query("DELETE FROM points_payment_per_day_remote_keys")
    suspend fun clearAllpaymentForProviderPerDayRemoteKeysTable()

    @Query("DELETE FROM points_payment_per_day_by_date_remote_keys_entity")
    suspend fun clearAllpaymentForProviderPerDayByDateRemoteKeysTable()

    @Query("DELETE FROM payment_for_provider_per_day")
    suspend fun clearAllpaymentForProviderPerDayTable()

    @Transaction
    @Query("SELECT * FROM payment_for_provider_per_day")
    fun getAllProfitPerDay() : PagingSource<Int, PaymentPerDayWithProvider>

    @Transaction
    @Query("SELECT * FROM payment_for_provider_per_day WHERE lastModifiedDate >= :beginDate AND lastModifiedDate <= :finalDate")
    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String) : PagingSource<Int, PaymentPerDayWithProvider>


}