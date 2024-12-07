package com.aymen.store.model.repository.remoteRepository.categoryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface CategoryRepository {


//    suspend fun getAllCategoryByCompany(companyId : Long): Response<List<CategoryDto>>
    suspend fun addCategoryApiWithImage(category : String, file : File)
    suspend fun addCategoryApiWithoutImeg(category:String)

    fun getAllCategory(companyId : Long): Flow<PagingData<Category>>
}