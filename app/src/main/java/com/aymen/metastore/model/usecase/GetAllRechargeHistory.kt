package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import kotlinx.coroutines.flow.Flow

class GetAllRechargeHistory(private val repository : PointPaymentRepository) {

    operator fun invoke(id : Long) : Flow<PagingData<PointsPayment>>{
        return repository.getAllRechargeHistory(id)
    }
}