package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyClient(private val repository: ClientRepository) {

     operator fun invoke(companyId : Long): Flow<PagingData<CompanyWithCompanyClient>> {
        return repository.getAllMyClient(companyId = companyId)
    }

}