package com.aymen.store.model.repository.remoteRepository.categoryRepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.paging.CategoryPagingSource
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CategoryRepositoryImpl  @Inject constructor(
    private val api : ServiceApi
)
    :CategoryRepository{
    override suspend fun getAllCategoryByCompany(companyId: Long) = api.getAllCategoryByCompany(companyId = companyId)
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

    override fun getAllCategory(pageSize: Int): Flow<PagingData<CategoryDto>> {
        return Pager(
            config = PagingConfig(pageSize= pageSize),
            pagingSourceFactory = {
                CategoryPagingSource(api)
            }
        ).flow
    }
}