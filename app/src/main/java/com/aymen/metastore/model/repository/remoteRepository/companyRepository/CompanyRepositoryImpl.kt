package com.aymen.metastore.model.repository.remoteRepository.companyRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.paging.CompanyRemoteMediator
import com.aymen.metastore.model.entity.paging.ProviderRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
)
    : CompanyRepository {

        private val companyDao = room.companyDao()
    private val clientProviderDao = room.clientProviderRelationDao()

    override suspend fun addCompany(company: String, file : File) {
        withContext(Dispatchers.IO){
            api.addCompany(
                company,
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyProvider(id: Long): Flow<PagingData<CompanyWithCompanyOrUser>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = ProviderRemoteMediator(
                api = api, room = room, id = id
            ),
            pagingSourceFactory = { clientProviderDao.getAllMyProviders(id)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
    override suspend fun getMyParent(companyId : Long) = api.getMyParent(companyId = companyId)
    override suspend fun getMeAsCompany() = api.getMeAsCompany()


    @OptIn(ExperimentalPagingApi::class)
    override fun getAllCompaniesContaining(search: String, searchType: SearchType) : Flow<PagingData<CompanyWithCompanyClient>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = CompanyRemoteMediator(
                api = api, room = room, categoryName = null, searchType = searchType, type = LoadType.CONTAINING, libelle = search, id = null
            ),
            pagingSourceFactory = { companyDao.getAllCompaniesContaining(search)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyClientContaining(
        id: Long,
        clientName: String
    ): Flow<PagingData<CompanyWithCompanyClient>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = CompanyRemoteMediator(
                api = api, room = room, categoryName = null, searchType = SearchType.MY, type = LoadType.RANDOM, libelle = clientName, id = id
            ),
            pagingSourceFactory = { clientProviderDao.getAllMyClientsContainig(id ,clientName)}
        ).flow.map {
            it.map { article ->
                article
            }
        }    }

    override suspend fun updateCompany(company: String, file: File) {
        withContext(Dispatchers.IO){
            api.updateCompany(
                company,
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
        }
    }
    override suspend fun updateImage(image: File): Response<Void> {
      return  api.updateImage(
            image = MultipartBody.Part
                .createFormData(
                    "file",
                    image.name,
                    image.asRequestBody()
                )
        )
    }


}