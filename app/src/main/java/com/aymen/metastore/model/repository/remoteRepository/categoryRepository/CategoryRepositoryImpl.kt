package com.aymen.store.model.repository.remoteRepository.categoryRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.paging.CategoryPagingSource
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.paging.CategoryRemoteMediator
import com.aymen.metastore.model.entity.paging.SubCategoryRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
    override suspend fun addCategoryApiWithImage(category: String, file: File) {
        api.addCategoryApiWithImage(category,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
        )
    }

    override suspend fun addCategoryApiWithoutImeg(category: String) {
        api.addCategoryApiWithoutImage(category)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllCategory(): Flow<PagingData<Category>> {
       return Pager(
           config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 2),
           remoteMediator = CategoryRemoteMediator(
               api = api, room = room, type = LoadType.RANDOM,id = sharedViewModel.company.value.id
           ),
           pagingSourceFactory = {
               categoryDao.getAllCategoriesByCompanyId(sharedViewModel.company.value.id!!)
           }
       ).flow.map {
           it.map { article ->
               article.toCategoryWithCompanyAndUser()
           }
       }
    }
}