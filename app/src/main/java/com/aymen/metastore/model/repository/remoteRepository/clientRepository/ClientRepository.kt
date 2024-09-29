package com.aymen.store.model.repository.remoteRepository.clientRepository

import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.realm.ClientProviderRelation
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.SearchHistory
import com.aymen.metastore.model.entity.realm.User
import retrofit2.Response
import java.io.File

interface ClientRepository {

    suspend fun getAllMyClient(companyId : Long) : Response<List<ClientProviderRelation>>

    suspend fun addClient(client: String, file : File)

    suspend fun addClientWithoutImage(client: String)

    suspend fun getAllMyClientContaining(clientName : String, companyId : Long):Response<List<ClientProviderRelation>>

    suspend fun sendClientRequest(id : Long, type : Type):Response<Void>

    suspend fun getAllClientContaining(search : String, searchType: SearchType, searchCategory: SearchCategory):Response<List<Company>>

    suspend fun getAllClientUserContaining(search : String, searchType: SearchType, searchCategory: SearchCategory):Response<List<User>>

    suspend fun saveHistory(category: SearchCategory, id: Long):Response<Void>

    suspend fun getAllHistory():Response<List<SearchHistory>>
}