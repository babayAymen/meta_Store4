package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PointsPayment
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.RechargeRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser

@Dao
interface PointsPaymentDao {

    @Upsert
    suspend fun insertPointsPayment(pointPayment : List<PointsPayment>)

    @Upsert
    suspend fun insertRechargeKeys(keys : List<RechargeRemoteKeysEntity>)

    @Upsert
    fun insertKeys(keys : List<PointsPaymentRemoteKeysEntity>)


    @Query("SELECT * FROM points_payment_remote_keys_entity WHERE id = :id")
    suspend fun getRemoteKeys(id : Long) : PointsPaymentRemoteKeysEntity?
    @Query("SELECT * FROM rechage_remote_keys_table WHERE id = :id")
    suspend fun getPointPaymentRemoteKey(id : Long) : RechargeRemoteKeysEntity

    @Query("DELETE FROM points_payment_remote_keys_entity")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM rechage_remote_keys_table")
    suspend fun clearPointsPaymentRemoteKeysTable()

    @Query("DELETE FROM points_payment")
    suspend fun clearPointsPayment()

    @Transaction
    @Query("SELECT * FROM payment_for_providers")
    fun getAllMyPointsPaymentForProviders() : PagingSource<Int, PaymentForProvidersWithCommandLine>

    @Transaction
     @Query("SELECT * FROM points_payment WHERE (clientCompanyId = :id OR providerId = :id OR clientUserId = :id)")
     fun getAllRechargeHistory(id : Long) : PagingSource<Int, PointsWithProviderclientcompanyanduser>

}