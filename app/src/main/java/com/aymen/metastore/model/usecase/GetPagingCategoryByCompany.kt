package com.aymen.metastore.model.usecase

import androidx.compose.foundation.pager.PageSize
import androidx.paging.PagingData
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetPagingCategoryByCompany(private val repository: CategoryRepository) {

    operator fun invoke(pageSize : Int): Flow<PagingData<CategoryDto>>{
        return repository.getAllCategory(pageSize)

    }
}