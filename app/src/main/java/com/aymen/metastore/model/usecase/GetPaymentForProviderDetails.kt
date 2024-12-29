package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.ReglementForProviderModel
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentForProviderDetails(private val repository: PointPaymentRepository) {
        operator fun invoke(paymentId : Long) : Flow<PagingData<ReglementForProviderModel>>{
            return repository.getPaymentForProviderDetails(paymentId)
        }
}