package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import kotlinx.coroutines.flow.Flow

class GetMyHistoryProfitByDate (private val repository: PointPaymentRepository) {

    operator fun invoke(id : Long , beginDate : String, finDate : String) : Flow<PagingData<PaymentPerDayWithProvider>>{
        return repository.getMyHistoryProfitByDate(id, beginDate, finDate)
    }
}