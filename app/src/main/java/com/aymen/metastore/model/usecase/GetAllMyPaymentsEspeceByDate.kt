package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyPaymentsEspeceByDate(private val repository: PointPaymentRepository) {

    operator fun invoke(id : Long, beginDate : String, finalDate : String):Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        return repository.getAllMyPaymentsEspeceByDate(id,beginDate, finalDate)
    }
}