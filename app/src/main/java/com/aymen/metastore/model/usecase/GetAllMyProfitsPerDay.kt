package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyProfitsPerDay(private val repository: PointPaymentRepository) {
    operator fun invoke(companyId : Long) : Flow<PagingData<PaymentPerDayWithProvider>>{
        return repository.getAllMyProfitsPerDay(companyId)
    }
}