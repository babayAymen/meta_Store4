package com.aymen.store.model.repository.remoteRepository.subCategoryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SubCategoryRepository {
     fun getSubCategoryByCategory(id : Long, companyId : Long): Flow<PagingData<SubCategory>>
    suspend fun addSubCtagoryWithImage(sousCategory : String, file : File)
    suspend fun addSubCategoryWithoutImage(sousCategory : String)
      fun getAllSubCategories(companyId : Long): Flow<PagingData<SubCategoryWithCategory>>
      fun getAllSubCategoriesByCompanyId(companyId : Long) : Flow<PagingData<SubCategoryWithCategory>>
}
