package com.aymen.store.model.repository.remoteRepository.clientRepository

import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ClientRepositoryImpl  @Inject constructor(
    private val api : ServiceApi
)
    :ClientRepository{

    override suspend fun getAllMyClient(companyId : Long) = api.getAllMyClient(companyId = companyId)
    override suspend fun getAllMyClientt(companyId : Long) = api.getAllMyClientt(companyId = companyId)
    override suspend fun addClient(client: String, file: File){
        withContext(Dispatchers.IO){
            api.addClient(
                client,
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
        }
    }

    override suspend fun addClientWithoutImage(client: String) = api.addClientWithoutImage(client)

    override suspend fun getAllMyClientContaining(clientName: String, companyId : Long) = api.getAllMyClientContaining(clientName = clientName, companyId = companyId)
    override suspend fun getAllMyClientContainingg(clientName: String, companyId : Long) = api.getAllMyClientContainingg(clientName = clientName, companyId = companyId)

    override suspend fun sendClientRequest(id: Long, type: Type) = api.sendClientRequest(id,type)
    override suspend fun getAllClientContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = api.getAllClientContaining(search, searchType,searchCategory)
    override suspend fun getAllClientContainingg(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = api.getAllClientContainingg(search, searchType,searchCategory)

    override suspend fun getAllClientUserContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = api.getAllUsersContaining(search, searchType,searchCategory)
    override suspend fun getAllClientUserContainingg(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = api.getAllUsersContainingg(search, searchType,searchCategory)

    override suspend fun saveHistory(category: SearchCategory, id: Long) = api.saveHistory(category, id)
    override suspend fun getAllHistory() = api.getAllHistory()
    override suspend fun getAllHistoryy() = api.getAllHistoryy()
}