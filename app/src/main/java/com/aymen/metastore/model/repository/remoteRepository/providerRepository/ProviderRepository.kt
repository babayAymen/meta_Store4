package com.aymen.store.model.repository.remoteRepository.providerRepository

import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import retrofit2.Response
import java.io.File

interface ProviderRepository {

    suspend fun addProvider(provider : String, file : File?):Response<ClientProviderRelationDto>
    suspend fun updateProvider(provider : String, file : File?):Response<CompanyDto>
    suspend fun deleteProvider(id : Long):Response<Void>
//    suspend fun addProviderWithoutImage(provider : String)

}