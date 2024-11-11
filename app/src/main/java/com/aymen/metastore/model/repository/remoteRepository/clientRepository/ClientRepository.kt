package com.aymen.store.model.repository.remoteRepository.clientRepository

import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.Dto.SearchHistoryDto
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import retrofit2.Response
import java.io.File

interface ClientRepository {

    suspend fun getAllMyClient(companyId : Long) : Response<List<ClientProviderRelationDto>>
    suspend fun addClient(client: String, file : File)
    suspend fun addClientWithoutImage(client: String)
    suspend fun getAllMyClientContaining(clientName : String, companyId : Long):Response<List<ClientProviderRelationDto>>
    suspend fun sendClientRequest(id : Long, type : Type):Response<Void>
    suspend fun getAllClientContaining(search : String, searchType: SearchType, searchCategory: SearchCategory):Response<List<CompanyDto>>
    suspend fun getAllClientUserContaining(search : String, searchType: SearchType, searchCategory: SearchCategory):Response<List<UserDto>>
     suspend fun saveHistory(category: SearchCategory, id: Long):Response<Void>
    suspend fun getAllHistory():Response<List<SearchHistoryDto>>
 }