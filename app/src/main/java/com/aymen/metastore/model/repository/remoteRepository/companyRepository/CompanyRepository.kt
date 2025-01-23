package com.aymen.store.model.repository.remoteRepository.companyRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.dto.InvitationDto
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface CompanyRepository {

    suspend fun addCompany(company: String, file : File)

     fun getAllMyProvider(companyId: Long, isAll : Boolean, search : String?): Flow<PagingData<ClientProviderRelation>>

    suspend fun getMyParent(companyId: Long): Response<CompanyDto>

    suspend fun getMeAsCompany(): Response<CompanyDto>

     fun getAllCompaniesContaining(search : String, searchType: SearchType, myId : Long): Flow<PagingData<CompanyDto>>
    suspend fun updateCompany(company: String , file : File)

    suspend fun updateImage(image : File) : Response<Void>
    suspend fun checkRelation(id : Long, accountType: AccountType) : Response<List<InvitationDto>>
}