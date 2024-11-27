package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyProviders(private val repository: CompanyRepository) {

    operator fun invoke(companyId : Long): Flow<PagingData<CompanyWithCompanyOrUser>>{
        return repository.getAllMyProvider(companyId)
    }
}