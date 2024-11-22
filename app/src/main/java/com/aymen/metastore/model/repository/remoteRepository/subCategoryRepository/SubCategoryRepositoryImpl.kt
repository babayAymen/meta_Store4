package com.aymen.store.model.repository.remoteRepository.subCategoryRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.paging.SubCategoryRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.repository.globalRepository.BaseRepository
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class SubCategoryRepositoryImpl  @Inject constructor(
    private val api : ServiceApi,
    private val sharedViewModel: SharedViewModel,
    private val room : AppDatabase
)
    :BaseRepository(),SubCategoryRepository{

        private val subCategoryDao = room.subCategoryDao()

    override suspend fun getSubCategoryByCategory(id : Long, companyId : Long) =  api.getAllSubCategoryByCategory(id, companyId = companyId)
    override suspend fun addSubCtagoryWithImage(sousCategory: String, file: File) {
        api.addSubCategoryWithImage(sousCategory,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
        )
    }

    override suspend fun addSubCategoryWithoutImage(sousCategory: String) = api.addSubCategoryWithoutImage(sousCategory)

    @OptIn(ExperimentalPagingApi::class)
    override  fun getAllSubCategories(): Flow<PagingData<SubCategoryWithCategory>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = SubCategoryRemoteMediator(
                api = api, room = room, type = LoadType.RANDOM,id = sharedViewModel.company.value.id
            ),
            pagingSourceFactory = { subCategoryDao.getAllSubCategories()}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

}