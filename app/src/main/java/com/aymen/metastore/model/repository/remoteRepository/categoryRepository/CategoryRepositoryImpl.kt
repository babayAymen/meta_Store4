package com.aymen.store.model.repository.remoteRepository.categoryRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.paging.pagingsource.CategoryPagingSource
import com.aymen.metastore.model.entity.paging.remotemediator.CategoryRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class CategoryRepositoryImpl  @Inject constructor(
    private val api : ServiceApi,
    private val sharedViewModel: SharedViewModel,
    private val room : AppDatabase
)
    :CategoryRepository{

        private val categoryDao = room.categoryDao()

//    override suspend fun getAllCategoryByCompany(companyId: Long) = api.getAllCategoryByCompany(companyId = companyId)
    override suspend fun addCategory(category: String, file: File?): Response<CategoryDto> {
       return if(file != null) api.addCategoryApiWithImage(category,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
        )
    else api.addCategoryApiWithoutImage(category)
    }

    override suspend fun updateCategory(category: String, file: File?): Response<CategoryDto> {
        return if(file == null)
            api.updateCategoryWithoutImage(category)
        else api.updateCategory(category,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllCategory(companyId : Long): Flow<PagingData<Category>> {
          return Pager(
           config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
           remoteMediator = CategoryRemoteMediator(
               api = api, room = room,id = companyId
           ),
           pagingSourceFactory = {
               categoryDao.getAllCategoriesByCompanyId(companyId)
           }
       ).flow.map {
           it.map { article ->
               article.toCategoryWithCompanyAndUser()
           }
       }
    }

    override fun getCategoryTemp(companyId: Long): Flow<PagingData<Category>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            pagingSourceFactory = {
                CategoryPagingSource(api, companyId)
            }
        ).flow
    }
}









