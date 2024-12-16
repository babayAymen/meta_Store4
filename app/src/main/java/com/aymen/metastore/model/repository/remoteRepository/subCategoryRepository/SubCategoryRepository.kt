package com.aymen.store.model.repository.remoteRepository.subCategoryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface SubCategoryRepository {
     fun getSubCategoryByCategory(id : Long, companyId : Long): Flow<PagingData<SubCategory>>
    suspend fun addSubCtagory(sousCategory : String, file : File?) : Response<SubCategoryDto>
    suspend fun updateSubCategory(sousCategory: String, file : File?) : Response<SubCategoryDto>
    fun getAllSubCategories(companyId : Long): Flow<PagingData<SubCategoryWithCategory>>
      fun getAllSubCategoriesByCompanyId(companyId : Long) : Flow<PagingData<SubCategoryWithCategory>>
}
