package com.aymen.store.model.repository.remoteRepository.clientRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface ClientRepository {

    fun getAllMyClient(companyId : Long) : Flow<PagingData<CompanyWithCompanyOrUser>>
    fun getAllClientUserContaining(companyId: Long, searchType: SearchType,search : String):Flow<PagingData<UserDto>>
    fun getMyClientForAutocompleteClient(companyId : Long , clientName : String) : Flow<PagingData<ClientProviderRelationDto>>

    suspend fun addClient(client: String, file : File)
    suspend fun addClientWithoutImage(client: String)
    suspend fun getAllMyClientContaining(clientName : String, companyId : Long):Response<List<ClientProviderRelationDto>>
    suspend fun sendClientRequest(id : Long, type : Type):Response<Void>
    suspend fun getAllClientContaining(search : String, searchType: SearchType, searchCategory: SearchCategory):Response<List<CompanyDto>>
    suspend fun saveHistory(category: SearchCategory, id: Long):Response<Void>
    fun getAllHistory(id : Long):Flow<PagingData<SearchHistoryWithClientOrProviderOrUserOrArticle>>
 }