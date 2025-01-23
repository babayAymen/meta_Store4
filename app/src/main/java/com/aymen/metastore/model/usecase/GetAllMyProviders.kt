package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyProviders(private val repository: CompanyRepository) {

    operator fun invoke(companyId : Long, isAll : Boolean, search : String?): Flow<PagingData<ClientProviderRelation>>{
        return repository.getAllMyProvider(companyId, isAll,search)
    }
}