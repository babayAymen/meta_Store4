package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Category
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetPagingCategoryByCompany(private val repository: CategoryRepository) {

    operator fun invoke(): Flow<PagingData<Category>>{
        return repository.getAllCategory()

    }
}