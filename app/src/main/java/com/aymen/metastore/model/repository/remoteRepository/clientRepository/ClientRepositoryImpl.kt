package com.aymen.store.model.repository.remoteRepository.clientRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.paging.AllPersonContainingPagingSource
import com.aymen.metastore.model.entity.paging.AllSearchRemoteMediator
import com.aymen.metastore.model.entity.paging.ClientRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
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
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ClientRepositoryImpl  @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
) :ClientRepository{

    private val clientProviderRelationDao = room.clientProviderRelationDao()
    private val searchHistoryDao = room.searchHistoryDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyClient(companyId: Long): Flow<PagingData<CompanyWithCompanyOrUser>> {
        Log.e("getallclient","company id : $companyId")
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
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
    override suspend fun getAllMyClientContaining(
        clientName: String,
        companyId: Long
    ): Response<List<ClientProviderRelationDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendClientRequest(id: Long, type: Type) = api.sendClientRequest(id,type)

    override suspend fun getAllClientContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = api.getAllClientContaining(search, searchType,searchCategory)


    override fun getAllClientUserContaining(
        companyId: Long,
        searchType: SearchType,
        search: String,
    ): Flow<PagingData<UserDto>> {
        Log.e("getAllPersonContaining","search call ")
        return  Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Number of items per page
                enablePlaceholders = false // Disable placeholders for unloaded pages
            ),
            pagingSourceFactory = {
                AllPersonContainingPagingSource(api,companyId, searchType,search)
            }
        ).flow
    }


    override suspend fun saveHistory(category: SearchCategory, id: Long) = api.saveHistory(category, id)
    @OptIn(ExperimentalPagingApi::class)
    override fun getAllHistory(id : Long):Flow<PagingData<SearchHistoryWithClientOrProviderOrUserOrArticle>>{
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = AllSearchRemoteMediator(
                api = api, room = room, id = id
            ),
            pagingSourceFactory = { searchHistoryDao.getAllSearchHistories() }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
 }