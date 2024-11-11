package com.aymen.store.model.repository.remoteRepository.companyRepository

import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.store.model.entity.dto.CompanyDto
import retrofit2.Response
import java.io.File

interface CompanyRepository {

    suspend fun addCompany(company: String, file : File)

    suspend fun getAllMyProvider(companyId: Long): Response<List<ClientProviderRelationDto>>

    suspend fun getMyParent(companyId: Long): Response<CompanyDto>

    suspend fun getMeAsCompany(): Response<CompanyDto>

    suspend fun getAllCompaniesContaining(search : String): Response<List<CompanyDto>>

    suspend fun updateCompany(company: String , file : File)

    suspend fun updateImage(image : File) : Response<Void>

}