package com.aymen.metastore.model.usecase

import android.util.Log
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyProviders(private val repository: CompanyRepository) {

    operator fun invoke(companyId : Long): Flow<PagingData<CompanyWithCompanyClient>>{
        return repository.getAllMyProvider(companyId)
    }
}