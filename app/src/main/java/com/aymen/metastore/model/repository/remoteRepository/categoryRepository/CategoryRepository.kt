package com.aymen.store.model.repository.remoteRepository.categoryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.model.Category
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface CategoryRepository {


//    suspend fun getAllCategoryByCompany(companyId : Long): Response<List<CategoryDto>>
    suspend fun addCategory(category : String, file : File?) : Response<CategoryDto>
   suspend fun updateCategory(category: String, file : File?)  : Response<CategoryDto>
    fun getAllCategory(companyId : Long): Flow<PagingData<Category>>
    fun getCategoryTemp(companyId : Long) : Flow<PagingData<Category>>
}