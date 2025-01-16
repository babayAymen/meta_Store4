package com.aymen.store.model.repository.remoteRepository.clientRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.dto.SearchHistoryDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface ClientRepository {

    fun getAllMyClient(companyId : Long) : Flow<PagingData<CompanyWithCompanyOrUser>>
    fun getAllClientUserContaining(companyId: Long, searchType: SearchType,search : String):Flow<PagingData<UserDto>>
    fun getMyClientForAutocompleteClient(companyId : Long , clientName : String) : Flow<PagingData<ClientProviderRelationDto>>

    suspend fun addClient(client: String, file : File?): Response<ClientProviderRelationDto>
    suspend fun updateClient(client: String, file : File?) :Response<CompanyDto>
    suspend fun deleteClient(relationId : Long) : Response<Void>
    suspend fun getAllMyClientContaining(clientName : String, companyId : Long):Response<List<ClientProviderRelationDto>>
    suspend fun sendClientRequest(id : Long, type : Type, isDeleted : Boolean):Response<Void>
    suspend fun getAllClientContaining(search : String, searchType: SearchType, searchCategory: SearchCategory):Response<List<CompanyDto>>
    suspend fun saveHistory(category: SearchCategory, id: Long):Response<SearchHistoryDto>
    suspend fun deleteSearch(id: Long):Response<Void>
    fun getAllHistory(id : Long):Flow<PagingData<SearchHistory>>
 }