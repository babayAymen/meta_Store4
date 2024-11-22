package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.PointsPayment
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser

@Dao
interface PointsPaymentDao {

    @Upsert
    suspend fun insertPointsPayment(pointPayment : List<PointsPayment>)

    @Upsert
    fun insertKeys(keys : List<PointsPaymentRemoteKeysEntity>)

    @Query("SELECT * FROM points_payment_remote_keys_entity WHERE id = :id")
    suspend fun getRemoteKeys(id : Long) : PointsPaymentRemoteKeysEntity?

    @Query("DELETE FROM points_payment_remote_keys_entity")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM points_payment")
    suspend fun clearPointsPayment()

    @Query("SELECT * FROM points_payment WHERE providerId = :companyId")
     fun getAllMyPointsPayment(companyId : Long) : PagingSource<Int, PaymentForProvidersWithCommandLine>
}