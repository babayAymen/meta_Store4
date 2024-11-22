package com.aymen.store.model.repository.remoteRepository.clientRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.paging.ClientRemoteMediator
import com.aymen.metastore.model.entity.paging.CompanyRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ClientRepositoryImpl  @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
) :ClientRepository{

    private val clientProviderRelationDao = room.clientProviderRelationDao()


    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyClient(companyId : Long) :Flow<PagingData<CompanyWithCompanyClient>>{
        Log.e("getallclient","company id : $companyId")
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = ClientRemoteMediator(
                api = api, room = room, id = companyId
            ),
            pagingSourceFactory = { clientProviderRelationDao.getAllMyClients(myCompanyId = companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


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
    override suspend fun sendClientRequest(id: Long, type: Type) = api.sendClientRequest(id,type)

    override suspend fun getAllClientContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = api.getAllClientContaining(search, searchType,searchCategory)


    @OptIn(ExperimentalPagingApi::class)
    override fun getAllClientUserContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) :Flow<PagingData<CompanyWithCompanyClient>>{
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = CompanyRemoteMediator(
                api = api, room = room, type = LoadType.RANDOM, categoryName= null, searchType = null, libelle = null
            ),
            pagingSourceFactory = { clientProviderRelationDao.getAllUserContaining(search) }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


    override suspend fun saveHistory(category: SearchCategory, id: Long) = api.saveHistory(category, id)
    override suspend fun getAllHistory() = api.getAllHistory()
 }