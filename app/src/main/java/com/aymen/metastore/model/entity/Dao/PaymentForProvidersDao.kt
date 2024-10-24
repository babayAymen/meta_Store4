package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PaymentForProviders

@Dao
interface PaymentForProvidersDao {

    @Upsert
    suspend fun insertPaymentForProviders(paymentForProviders: PaymentForProviders)


}