package com.aymen.metastore.model.repository.remoteRepository.subCategoryRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.paging.SubCategoryByCompanyIdRemoteMediator
import com.aymen.metastore.model.entity.paging.SubCategoryPagingSource
import com.aymen.metastore.model.entity.paging.SubCategoryRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.repository.globalRepository.BaseRepository
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.store.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
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
) :BaseRepository(), SubCategoryRepository {

        private val subCategoryDao = room.subCategoryDao()

    override fun getSubCategoryByCategory(id : Long):Flow<PagingData<SubCategory>>{
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Number of items per page
                enablePlaceholders = false // Disable placeholders for unloaded pages
            ),
            pagingSourceFactory = {
                SubCategoryPagingSource(api, sharedViewModel, id)
            }
        ).flow
    }


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
    override  fun getAllSubCategories(companyId: Long): Flow<PagingData<SubCategoryWithCategory>> {
        Log.e("subcategoryviewModel","call getAllSubCategoriesByCompanyId2")
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = SubCategoryRemoteMediator(
                api = api, room = room,id = companyId
            ),
            pagingSourceFactory = { subCategoryDao.getAllSubCategories(companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllSubCategoriesByCompanyId(companyId: Long): Flow<PagingData<SubCategoryWithCategory>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = SubCategoryByCompanyIdRemoteMediator(
                api = api, room = room,id = sharedViewModel.company.value.id
            ),
            pagingSourceFactory = { subCategoryDao.getAllSubCategories(companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


}