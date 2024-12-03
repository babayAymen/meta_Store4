package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllSubCategoryByCategoryId(private val repository: SubCategoryRepository) {

    operator fun invoke(categoryId : Long) : Flow<PagingData<SubCategory>>{
        return repository.getSubCategoryByCategory(categoryId)
    }
}