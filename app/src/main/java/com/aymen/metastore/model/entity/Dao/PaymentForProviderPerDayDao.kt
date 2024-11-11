package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider

@Dao
interface PaymentForProviderPerDayDao {

    @Upsert
    suspend fun insertPaymentForProviderPerDay(payment : PaymentForProviderPerDay)

    @Query("SELECT * FROM payment_for_provider_per_day")
    suspend fun getAllMyProfits(): List<PaymentPerDayWithProvider>

    @Query("SELECT * FROM payment_for_provider_per_day WHERE lastModifiedDate BETWEEN :begin AND :end")
    suspend fun getAllMyProfitsByDate(begin : String , end : String): List<PaymentPerDayWithProvider>
}