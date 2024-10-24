package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PointsPayment

@Dao
interface PointsPaymentDao {

    @Upsert
    suspend fun insertPointsPayment(pointPayment : PointsPayment)
}