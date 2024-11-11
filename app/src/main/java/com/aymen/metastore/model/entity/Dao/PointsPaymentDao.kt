package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PointsPayment
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser

@Dao
interface PointsPaymentDao {

    @Upsert
    suspend fun insertPointsPayment(pointPayment : PointsPayment)

    @Query("SELECT * FROM points_payment")
    suspend fun getAllMyointsPayment() : List<PointsWithProviderclientcompanyanduser>
}