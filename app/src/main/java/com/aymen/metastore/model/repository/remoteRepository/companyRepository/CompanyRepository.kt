package com.aymen.store.model.repository.remoteRepository.companyRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface CompanyRepository {

    suspend fun addCompany(company: String, file : File)

     fun getAllMyProvider(companyId: Long): Flow<PagingData<CompanyWithCompanyClient>>

    suspend fun getMyParent(companyId: Long): Response<CompanyDto>

    suspend fun getMeAsCompany(): Response<CompanyDto>

     fun getAllCompaniesContaining(search : String, searchType: SearchType): Flow<PagingData<CompanyWithCompanyClient>>
    fun getAllMyClientContaining(id : Long , clientName : String): Flow<PagingData<CompanyWithCompanyClient>>
    suspend fun updateCompany(company: String , file : File)

    suspend fun updateImage(image : File) : Response<Void>

}