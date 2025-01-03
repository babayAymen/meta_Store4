package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyInvitations(private val repository : InvetationRepository) {

    operator fun invoke(companyId : Long) : Flow<PagingData<Invitation>>{
        return repository.getAllMyInvetations(companyId)
    }
}