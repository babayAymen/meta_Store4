package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyPaymentNotAccepted(private val repository: PointPaymentRepository) {

//     operator fun invoke() : Flow<PagingData<PointsWithProviderclientcompanyanduser>>{
//        return repository.getAllMyPaymentNotAccepted()
//    }

}