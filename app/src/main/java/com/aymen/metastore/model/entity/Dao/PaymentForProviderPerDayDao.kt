package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PaymentForProviderPerDay

@Dao
interface PaymentForProviderPerDayDao {

    @Upsert
    suspend fun insertPaymentForProviderPerDay(payment : PaymentForProviderPerDay)
}