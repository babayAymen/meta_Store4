package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.PaymentForProviders
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import java.util.Date

@Dao
interface PaymentForProvidersDao {

    @Upsert
    suspend fun insertPaymentForProviders(paymentForProviders: PaymentForProviders)

    @Query("SELECT * FROM payment_for_providers WHERE createdDate BETWEEN :date AND :finDate")
    suspend fun getMyPaymentByDate(date: String , finDate : String): List<PaymentForProvidersWithCommandLine>

    @Query("SELECT * from payment_for_providers")
    suspend fun getAllMyPaymentsEspece():List<PaymentForProvidersWithCommandLine>

//    suspend fun getAllMyPaymentNotAccepted(id : Long):List<PaymentForProviders>
}